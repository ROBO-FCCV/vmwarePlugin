#!/bin/bash
logfile_path="/var/log/plugin"
vmware_information_conf=/opt/plugin/conf/vmware_information.conf
mkdir -p /var/log/plugin
touch ${logfile_path}/registry_cps.log
function log_info ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo "${DATE_N} ${USER_N} execute $0 [INFO] $@" >>${logfile_path}/registry_cps.log
}

function log_error ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo -e "\033[41;37m ${DATE_N} ${USER_N} execute $0 [ERROR]] $@ \033[0m"  >>${logfile_path}/registry_cps.log
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

function check(){
    if [[ ! -f ${vmware_information_conf} ]];then
        echo "The plugin is not installed"
        exit 0
    fi
}

function cps_check(){
    ps -ef |grep -v grep |grep /opt/object_name/robo/cps-monitor/cps_monitor/cps_monitor.py >> /dev/null 2>&1
    fn_log "cps-monitor health check"
}

function registry_check(){
    reg_command_file="/opt/object_name/robo/cps-monitor/cps_monitor/reg_monitor_cli.py"
    cps_conf=/opt/object_name/robo/etc/cps-monitor/cps/components/agent
    for item in `grep "vmware_name" ${vmware_information_conf} | awk -F"=" '{print $2}'`;do
        if [[ ! `ls ${cps_conf}` =~ $item ]];then
            ${reg_command_file} reg ${item} --type 1 --cmd "/opt/plugin/${item}" --proc-num 1 --heartbeat-timeout 20 --heartbeat-interval 15 --no-control  --restart-script "sudo /bin/systemctl restart ${item}"
            echo "robouser ALL=(root) NOPASSWD: /bin/systemctl restart ${item}" >> /etc/sudoers.d/robouser
            echo "" >> /etc/sudoers.d/robouser
        fi
    done
}

function registry_cps(){
    check
    cps_check
    registry_check
    echo "Registration is complete."
}

registry_cps