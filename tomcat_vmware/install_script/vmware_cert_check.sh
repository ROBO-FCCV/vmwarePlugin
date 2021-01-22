#!/bin/bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

CURRENT_DIR=$(cd "$(dirname "$0")";pwd)
cert_home_plugin_path=/opt/plugin/cert
tomcat_vmware_cert_path="${cert_home_plugin_path}/vmware_plugin/keystore.jks"
logfile_path="/var/log/plugin/vmware_plugin_certificate_check.log"

alarm_dir=/opt/object_name/robo/sbin/certificate
expire_days=30
localtime=`date +%s`

if [[ ! -f ${logfile_path} ]];then
    touch ${logfile_path}
    chmod 640 ${logfile_path}
fi

function send_alarm()
{
    alarm_type=$1
    alarm_info=$2
    alarm_resource=$3
    if [[ "${alarm_type}" == "certificate expired" ]];then
        alarm_id="0x0000030000010002"
    else
        alarm_id="0x0000030000010003"
    fi
    ${alarm_dir}/alarm.py insert ${alarm_id} "${alarm_type}" "${alarm_resource}" "${alarm_info}"
}

function delete_alarm()
{
    alarm_type=$1
    alarm_resource=$2
    if [[ "${alarm_type}" == "certificate expired" ]];then
        alarm_id="0x0000030000010002"
    else
        alarm_id="0x0000030000010003"
    fi
    ${alarm_dir}/alarm.py delete ${alarm_id} "${alarm_type}" "${alarm_resource}"
}

function log_info()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today INFO: $1." >> ${logfile_path}
}

function log_err()
{
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today ERROR: $1." >> ${logfile_path}
}

function check_date()
{
    local expdateseconds=$1
    local cert_path=$2
    local days=`expr ${expdateseconds} - ${localtime}`
    alarm_cert_name=$(echo "${cert_path}" | awk -F '/cert/' '{print $NF}')
    if [[ ${days} -lt 0 ]];then
        log_info "cert:${cert_path} is expired."
        echo "cert ${cert_path} is expired."
        send_alarm "certificate expired" "cert:${alarm_cert_name} is expired." "${alarm_cert_name}"
        return 1
    else
        delete_alarm "certificate expired" "${alarm_cert_name}"
    fi

    days=`expr ${days} / 86400`
    if [[ $days -lt ${expire_days} ]];then
        log_info "cert:${cert_path} will be expired in ${days} days."
        echo "cert ${cert_path} will be expired in ${days} days."
        send_alarm "certificate nearly expired" "cert:${alarm_cert_name} will be expired in ${days} days." "${alarm_cert_name}"
        return 1
    else
        delete_alarm "certificate nearly expired" "${alarm_cert_name}"
    fi
    echo "cert ${cert_path} is valid."
    return 0
}

function check_cert_jks()
{
    local cert_path=$1

    entry_list=`keytool -list -keystore ${cert_path} -protected |grep 'Entry,' |awk -F',' '{print $1}' 2>> /dev/null`
    tmp_dir="/tmp/certificate_check_`openssl rand -hex 5`"
    mkdir -p ${tmp_dir}
    for cert in ${entry_list}; do
        rfc_cert="cert_`openssl rand -hex 5`"
        keytool -list -keystore ${cert_path} -protected -alias ${cert} -rfc >> ${tmp_dir}/${rfc_cert} 2>> /dev/null
        grep "CERTIFICATE" ${tmp_dir}/${rfc_cert} >> /dev/null 2>&1
        if [[ $? -ne 0 ]]; then
            log_err "Rfc_cert generation failed"
            continue
        fi
        expire_time=`openssl x509 -enddate -noout -in ${tmp_dir}/${rfc_cert} |awk -F= '{print $2}'`
        expire_time_in_seconds=`date -d "${expire_time}" +%s`
        check_date ${expire_time_in_seconds} "${cert_path}:${cert}"
    done
    rm -rf /tmp/certificate_check_*
}


check_cert_jks ${tomcat_vmware_cert_path}
log_info "all certs checked."


