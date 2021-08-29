#!/bin/bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

source /opt/plugin/vmware_plugin/sbin/cert_gen.sh
cert_path=/opt/plugin/cert/vmware_plugin
cert_logfilePath="/var/log/plugin/replace_certs.log"
service_name=vmware_plugin
install_dir=/opt/plugin/vmware_plugin
current_dir=$(cd `dirname $0`; pwd)
source ${current_dir}/file_check.sh
if [[ ! -f ${cert_logfilePath} ]];then
    touch ${cert_logfilePath}
fi

function pre_file()
{
    rm -rf ${cert_path}
    mkdir -p ${cert_path}/temp
}

function log_info ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo "${DATE_N} ${USER_N} execute $0 [[INFO]] $@" >> ${cert_logfilePath}
}
function log_error ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo -e "\033[41;37m ${DATE_N} ${USER_N} execute $0 [[ERROR]] $@ \033[0m"  >>${cert_logfilePath}
}
function change_keystore_password()
{
    local security_cfg_file=${cert_path}/${service_name}.conf
    check_link_current_path ${security_cfg_file}
    if [[ $? -ne 0 ]]; then
       log_error "The file ${security_cfg_file} is not secure."
       exit 1
    fi
    local en_pass=`grep "^truststore=" ${security_cfg_file}|awk -F "=" '{print $2}'`
    if [[ $? -ne 0 ]]; then
       log_error "Get keystore password fail."
       exit 1
    fi
    local de_pass=`python -c "import kmc.kmc;import os;os.environ['KMC_DATA_USER']='vmware';print(kmc.kmc.API().decrypt(0, '${en_pass}'))"`
    if [[ $? -ne 0 ]]; then
       log_error "Decrypt keystore password fail."
       exit 1
    fi
    log_info "Decrypt keystore password successfully."
    rm -f ${current_dir}/temp_pass.info
    expect > ${current_dir}/temp_pass.info << EOF
spawn java -classpath ${install_dir}/sbin/commons-codec-1.15.jar:${install_dir}/sbin/Http11Protocol.jar com.object_name.scbb.tomcat.http11.KeyMgr
expect {
"keyStorePass:" {send "${de_pass}\r";exp_continue}
}
catch wait result;
puts \$result;
exit [lindex \$result 3]
expect eof;
EOF
    if [[ $? -ne 0 ]]; then
        log_error  "Get tomccat keystore password file fail."
        exit 1
    fi
    local key=`grep "keystorePass=" ${current_dir}/temp_pass.info`
    if [[ $? -ne 0 ]]; then
        rm -rf  ${current_dir}/temp_pass.info
        log_error  "Get keystorePass error"
        exit 1
    fi
    tomcat_key=${key#*=}
    log_info  "Get keystorePass successfully"
    local key_material=`grep "tomcat.connector.https.keyMaterial=" ${current_dir}/temp_pass.info`
    if [[ $? -ne 0 ]]; then
        rm -rf ${current_dir}/temp_pass.info
        log_error  "Get keyMaterial error"
        exit 1
    fi
    tomcat_key_material=${key_material#*=}
    rm -f ${current_dir}/temp_pass.info
    log_info "Get keyMaterial successfully"
    su - vmware -s /bin/bash >>${cert_logfilePath} 2>&1 << EOF
    sed -i "/keystorePass=/c keystorePass=\"${tomcat_key}\"" ${install_dir}/tomcat/conf/server.xml
    sed -i s#maxPostSize\=\"53687091200\"#maxPostSize\=\"1024\"#g ${install_dir}/tomcat/conf/server.xml
    sed -i "/tomcat.connector.https.keyMaterial/c tomcat.connector.https.keyMaterial=${tomcat_key_material}" ${install_dir}/tomcat/conf/catalina.properties
    dos2unix ${install_dir}/tomcat/conf/catalina.properties > /dev/null 2>&1
    dos2unix ${install_dir}/tomcat/conf/server.xml > /dev/null 2>&1
EOF
    log_info "Update password successfully."
}

function gen_cert()
{
    gen_tomcat_plugin_cert ${service_name} ${cert_path}/temp
    if [[ $? -ne 0 ]];then
        log_error "gen_server_cert failed"
        exit 1
    fi
    if [[ -f ${cert_path}/temp/server.keystore.jks ]];then
        check_link_full_path ${cert_path}
        if [[ $? -ne 0 ]]; then
            log_error "gen_server_cert failed"
            rm -rf ${cert_path}/temp
            exit 1
        fi
        rm -f ${cert_path}/keystore.jks
        cp ${cert_path}/temp/server.keystore.jks ${cert_path}/keystore.jks
        chmod 600 ${cert_path}/keystore.jks
        config_store_password ${cert_path}/${service_name}.conf
        chown vmware:plugin -R -h ${cert_path}
        chmod 600 ${cert_path}/${service_name}.conf
        rm -rf ${cert_path}/temp
        log_info "Replace vmware_plugin certification successfully."
    else
        log_error "create ${service_name} cert failed"
        exit 1
    fi
    change_keystore_password
}

pre_file
gen_cert
systemctl restart vmware_plugin
