#!/bin/bash
CurrentDir=$(dirname $0)
if [ ${CurrentDir} == "." ];then
    CurrentDir=$PWD
fi
top_dir=$(dirname ${CurrentDir})
base_dir2=`grep "install_path" ${top_dir}/conf/install.conf |awk -F= '{print $2}'`
logfilePath="/var/log/plugin/vmware_plugin/install_vmware_plugin.log"
SECURITY_PATH="${base_dir2}/security"
SUBJECT_PRE="/C=CN/ST=SiChuan/O=object_name/CN="
LOCAL_IP=127.0.0.1

ENCRYPT_KEYSTORE_PASS=`grep keystore_password ${base_dir2}/security/priv/security.conf |awk -F= '{print $2}'`
ENCRYPT_PRIVATE_KEY_PASSWORD=`grep pkey_password ${base_dir2}/security/priv/priv.conf |awk -F= '{print $2}'`
STORE_PASSWORD=`python -c "import kmc.kmc as K; print K.API().decrypt(0, '${ENCRYPT_KEYSTORE_PASS}')"`
PRIVATE_KEY_PASSWORD=`python -c "import kmc.kmc as K; print K.API().decrypt(0, '${ENCRYPT_PRIVATE_KEY_PASSWORD}')"`

SUBJECT_JKS="OU=object_name,O=object_name,L=Chengdu,ST=SiChuan,C=CN"

log_err()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today ERROR: $1." >> $logfilePath
}

log_info()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today INFO: $1." >> $logfilePath
}

function gen_tomcat_plugin_cert()
{
    if [ $# -ne 2 ];then
        log_err "gen_tomcat_plugin_cert, args number incorrect."
        return 1
    fi
    local service_name=$1
    local dest_path=$2
    mkdir -p ${dest_path}

    keytool -keystore ${dest_path}/server.keystore.jks -alias tomcat-server -validity 3650 -genkey -keyalg RSA -keysize 2048 -storepass ${STORE_PASSWORD} -keypass ${STORE_PASSWORD} -dname "CN=${LOCAL_IP},"${SUBJECT_JKS} >/dev/null 2>&1
    if [ $? -ne 0 ];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    keytool -keystore ${dest_path}/server.keystore.jks -alias tomcat-server -certreq -file ${dest_path}/cert-file -storepass ${STORE_PASSWORD} -keypass ${STORE_PASSWORD} >/dev/null 2>&1
    if [ $? -ne 0 ];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    openssl x509 -req -CA ${SECURITY_PATH}/server_ca/ca.pem -CAkey ${SECURITY_PATH}/server_ca/ca.key -in ${dest_path}/cert-file -out ${dest_path}/cert-signed -days 3650 -CAcreateserial -passin pass:${PRIVATE_KEY_PASSWORD} >/dev/null 2>&1
    if [ $? -ne 0 ];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    keytool -keystore ${dest_path}/server.keystore.jks -alias CARoot -import -file ${SECURITY_PATH}/server_ca/ca.pem -storepass ${STORE_PASSWORD} -keypass ${STORE_PASSWORD} -noprompt >/dev/null 2>&1
    if [ $? -ne 0 ];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    keytool -keystore ${dest_path}/server.keystore.jks -alias tomcat-server -import -file ${dest_path}/cert-signed -storepass ${STORE_PASSWORD} -keypass ${STORE_PASSWORD} >/dev/null 2>&1
    if [ $? -ne 0 ];then
        log_err "create ${service_name} cert failed"
        return 1
    fi
    return 0
}