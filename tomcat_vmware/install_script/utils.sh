#!/bin/bash
logfilePath="/var/log/plugin/vmware_plugin"
mkdir -p /var/log/plugin/vmware_plugin
touch ${logfilePath}/install_vmware_plugin.log
chmod 640 ${logfilePath}/install_vmware_plugin.log
function log_info ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo "${DATE_N} ${USER_N} execute $0 [INFO] $@" >>${logfilePath}/install_vmware_plugin.log
}

function log_error ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo -e "\033[41;37m ${DATE_N} ${USER_N} execute $0 [ERROR] $@ \033[0m"  >>${logfilePath}/install_vmware_plugin.log
}

function fn_log ()  {
    if [  $? -eq 0  ]
    then
        log_info "$@ succeeded."
    else
        log_error "$@ failed."
        echo -e "\033[41;37m $@ failed. \033[0m"
        exit 1
    fi
}