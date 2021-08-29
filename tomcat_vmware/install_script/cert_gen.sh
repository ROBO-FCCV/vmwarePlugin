#!/bin/bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

current_cert_dir=$(dirname $0)
if [[ ${current_cert_dir} == "." ]];then
    current_cert_dir=$PWD
fi
top_dir=$(dirname ${current_cert_dir})
base_dir2=`grep ^install_path ${top_dir}/conf/install.conf |awk -F= '{print $2}'`
inner_dir=`grep ^install_path ${top_dir}/conf/install.conf |awk -F'/' '{print $3}'`
logfile_path="/var/log/plugin/install_vmware_plugin.log"
SECURITY_PATH="${base_dir2}/security"
SUBJECT_PRE="/C=CN/ST=SiChuan/O=object_name/CN="
LOCAL_IP=127.0.0.1
cert_validity_days=3650
STORE_PASSWORD=`mkpasswd -l 16 -s 0`

CA_PASSWORD=`python -c "import kmc.kmc as K;import os;os.environ['KMC_DATA_USER']='tomcat'; print(K.API().decrypt(0, '$(grep ^private_key= /opt/${inner_dir}/robo/security/priv/tomcat.conf |awk -F= '{print $2}')'))"`
export KMC_PYTHON_PLAIN_TEXT=${STORE_PASSWORD}
ENCRYPT_KEYSTORE_PASS=`python -c "import kmc.kmc as K;import os;os.environ['KMC_DATA_USER']='vmware'; print(K.API().encrypt(0))"`
unset KMC_PYTHON_PLAIN_TEXT
SUBJECT_JKS="OU=object_name,O=object_name,L=Chengdu,ST=SiChuan,C=CN"

log_err()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today ERROR: $1." >> ${logfile_path}
}

log_info()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today INFO: $1." >> ${logfile_path}
}

function config_store_password(){
    local file_name=$1
    rm -f ${file_name}
    echo "truststore=${ENCRYPT_KEYSTORE_PASS}" >> ${file_name}
}

function gen_tomcat_plugin_cert()
{
    if [[ $# -ne 2 ]];then
        log_err "gen_tomcat_plugin_cert, args number incorrect."
        return 1
    fi
    local service_name=$1
    local dest_path=$2
    mkdir -p ${dest_path}

    keytool -keystore ${dest_path}/server.keystore.jks -alias tomcat-server -validity ${cert_validity_days} -genkey -keyalg RSA -keysize 2048 -dname "CN=${LOCAL_IP},"${SUBJECT_JKS} >/dev/null 2>&1  <<EOF
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
EOF
    if [[ $? -ne 0 ]];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    keytool -keystore ${dest_path}/server.keystore.jks -alias tomcat-server -certreq -file ${dest_path}/cert-file >/dev/null 2>&1 <<EOF
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
EOF
    if [[ $? -ne 0 ]];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    expect >/dev/null 2>&1 << EOF
spawn openssl x509 -req -CA ${SECURITY_PATH}/server_ca/ca.pem -CAkey ${SECURITY_PATH}/server_ca/ca.key -in ${dest_path}/cert-file -out ${dest_path}/cert-signed -days ${cert_validity_days} -extensions v3_req -extfile ${top_dir}/sbin/openssl.cnf -CAcreateserial
expect {
"ca.key" {send "${CA_PASSWORD}\r";exp_continue}
}
exit
expect eof;
EOF
    if [[ $? -ne 0 ]];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    keytool -keystore ${dest_path}/server.keystore.jks -alias CARoot -import -file ${SECURITY_PATH}/server_ca/ca.pem -noprompt >/dev/null 2>&1 <<EOF
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
EOF
    if [[ $? -ne 0 ]];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    keytool -keystore ${dest_path}/server.keystore.jks -alias tomcat-server -import -file ${dest_path}/cert-signed >/dev/null 2>&1 <<EOF
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
${STORE_PASSWORD}
EOF
    if [[ $? -ne 0 ]];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    rm -f ${SECURITY_PATH}/server_ca/ca.srl
    return 0
}
