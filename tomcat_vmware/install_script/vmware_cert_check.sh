#!/bin/bash
CURRENT_DIR=$(cd "$(dirname "$0")";pwd)
CERT_HOME_PLUGIN_PATH=/opt/plugin/cert
TOMCAT_VMWARE_CERT_PATH="${CERT_HOME_PLUGIN_PATH}/vmware_plugin/keystore.jks"
logfilePath="/var/log/plugin/vmware_plugin/certificate_check.log"

ENCRYPT_KEYSTORE_PASS=$(grep keystore_password /opt/object_name/robo/security/priv/security.conf |awk -F= '{print $2}')
STORE_PASSWORD=`python -c "import kmc.kmc as K; print K.API().decrypt(0, '${ENCRYPT_KEYSTORE_PASS}')"`

ALARM_DIR=/opt/object_name/robo/sbin/certificate
EXPIRE_DAYS=30
localtime=`date +%s`

if [ ! -f ${logfilePath} ];then
    touch ${logfilePath}
    chmod 640 ${logfilePath}
fi

function send_alarm()
{
    alarm_type=$1
    alarm_info=$2
    alarm_resource=$3
    if [ "${alarm_type}" == "certificate expired" ];then
        alarm_id=00500010
    else
        alarm_id=00500011
    fi
    ${ALARM_DIR}/alarm.py insert ${alarm_id} "${alarm_type}" "${alarm_resource}" "${alarm_info}"
}

function delete_alarm()
{
    alarm_type=$1
    alarm_resource=$2
    if [ "${alarm_type}" == "certificate expired" ];then
        alarm_id=00500010
    else
        alarm_id=00500011
    fi
    ${ALARM_DIR}/alarm.py delete ${alarm_id} "${alarm_type}" "${alarm_resource}"
}

function log_info()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today INFO: $1." >> $logfilePath
}

function log_err()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today ERROR: $1." >> $logfilePath
}

function check_date()
{
    local expdateseconds=$1
    local cert_path=$2
    local days=`expr $expdateseconds - ${localtime}`
    if [[ $days -lt 0 ]];then
        log_info "cert:${cert_path} is expired."
        echo "cert ${cert_path} is expired."
        send_alarm "certificate expired" "cert:${cert_path} is expired." "${cert_path}"
        return 1
    else
        delete_alarm "certificate expired" "${cert_path}"
    fi

    days=`expr $days / 86400`
    if [[ $days -lt ${EXPIRE_DAYS} ]];then
        log_info "cert:${cert_path} will be expired in ${days} days."
        echo "cert ${cert_path} will be expired in ${days} days."
        send_alarm "certificate nearly expired" "cert:${cert_path} will be expired in ${days} days." "${cert_path}"
        return 1
    else
        delete_alarm "certificate nearly expired" "${cert_path}"
    fi
    echo "cert ${cert_path} is valid."
    return 0
}

function check_cert_jks()
{
    local cert_path=$1
    local passwd=$2

    entry_list=`keytool -list -keystore ${cert_path} -storepass ${passwd} |grep 'Entry,' |awk -F',' '{print $1}' 2>> /dev/null`
    tmp_dir="/tmp/certificate_check_`openssl rand -hex 5`"
    mkdir -p ${tmp_dir}
    for cert in ${entry_list}; do
        rfc_cert="cert_`openssl rand -hex 5`"
        keytool -list -keystore ${cert_path} -storepass ${passwd} -alias ${cert} -rfc >> ${tmp_dir}/${rfc_cert} 2>> /dev/null
        expire_time=`openssl x509 -enddate -noout -in ${tmp_dir}/${rfc_cert} |awk -F= '{print $2}'`
        expire_time_in_seconds=`date -d "${expire_time}" +%s`
        check_date ${expire_time_in_seconds} "${cert_path}:${cert}"
    done
    rm -rf /tmp/certificate_check_*
}


check_cert_jks ${TOMCAT_VMWARE_CERT_PATH} ${STORE_PASSWORD}
log_info "all certs checked."


