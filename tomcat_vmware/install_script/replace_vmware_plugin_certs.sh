#!/bin/bash
source /opt/plugin/vmware_plugin/sbin/cert_gen.sh
cert_path=/opt/plugin/cert/vmware_plugin
cert_logfilePath="/var/log/plugin/replace_certs.log"
if [ ! -f ${cert_logfilePath} ];then
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
    echo "${DATE_N} ${USER_N} execute $0 [INFO] $@" >> ${cert_logfilePath}
}

function log_error ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo -e "\033[41;37m ${DATE_N} ${USER_N} execute $0 [ERROR] $@ \033[0m"  >>${cert_logfilePath}
}

function gen_cert()
{
    service_name=vmware_plugin
    gen_tomcat_plugin_cert $service_name ${cert_path}/temp
    if [ $? -ne 0 ];then
        log_error "gen_server_cert failed"
        exit 1
    fi
    if [ -f ${cert_path}/temp/*keystore.jks ];then
        cp $cert_path/temp/*keystore.jks $cert_path/keystore.jks
        chmod 600 $cert_path/keystore.jks
        chown vmware:plugin -R $cert_path
        rm -rf ${cert_path}/temp
        log_info "Replace vmware_plugin certification successfully."
    else
        log_error "create ${service_name} cert failed"
        exit 1
    fi
}

pre_file
gen_cert
systemctl restart vmware_plugin
