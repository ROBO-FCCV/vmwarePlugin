#!/bin/bash

logfile_path="/var/log/plugin/tomcat_https"
mkdir -p /var/log/plugin/tomcat_https
touch ${logfile_path}/update_plugin.log
function log_info ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo "${DATE_N} ${USER_N} execute $0 [INFO] $@" >>${logfile_path}/update_plugin.log
}

function log_error ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo -e "\033[41;37m ${DATE_N} ${USER_N} execute $0 [ERROR] $@ \033[0m"  >>${logfile_path}/update_plugin.log
}

function fn_log ()  {
    if [[  $? -eq 0  ]]
    then
        log_info "$@ succeeded."
    else
        log_error "$@ failed."
        echo -e "\033[41;37m $@ failed. \033[0m"
        exit 1
    fi
}