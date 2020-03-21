#!/bin/bash
currentDir=$(cd `dirname $0`; pwd)
if [ ${currentDir} == "." ];then
    currentDir=$PWD
fi
base_dir=$(dirname ${currentDir})
top_dir=$(dirname $(dirname ${currentDir}))
check_path=${currentDir}/vmware_cert_check.sh
log_dire=/var/log/plugin
CRONTAB_FILE=/etc/cron.d/FusionCube
vmware_information_conf=/opt/plugin/conf/vmware_information.conf
mkdir -p $log_dire
str=$1
plugin_service=$2
para_num=$#

function check_mode(){
    if [[ ! $currentDir =~ "/opt/plugin" ]];then
        return 0
    fi
    if [[ ! $currentDir =~ "/sbin" ]];then
        return 0
    fi
    conf_file=${base_dir}/conf/install.conf
    serviceName=`grep vmware_name ${conf_file} | awk -F"=" '{print $2}'`
    logfilePath=${log_dire}/uninstall_${serviceName}.log
    touch $logfilePath
    return 1
}
function log_info ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo "${DATE_N} ${USER_N} execute $0 [INFO] $@" >>$logfilePath
}

function log_error ()
{
    DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
    USER_N=`whoami`
    echo -e "\033[41;37m ${DATE_N} ${USER_N} execute $0 [ERROR] $@ \033[0m"  >>$logfilePath
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

function echoman () {
echo "Usage: sh uninstall.sh [--service <service> | --all]"
}

function check(){
    if [ ! -f ${vmware_information_conf} ];then
        echo "The plugin is not installed"
        exit 0
    fi
}

function check_input() {
    VMware_names=`grep "vmware_name" ${vmware_information_conf} | awk -F"=" '{print $2}' | xargs`
    VMware_item=$1
    for each in ${VMware_names[@]};do
        if [ $each = $VMware_item ];then
            return 0
        fi
    done
    return 1
}

function show_item(){
    grep "vmware_name" ${vmware_information_conf} >> /dev/null 2>&1
    if [ $? -ne 0 ];then
        echo "The plugin is not installed"
        exit 1
    fi
    VMware_names=`grep "vmware_name" ${vmware_information_conf} | awk -F"=" '{print $2}'`
    echo "************************************"
    echo $VMware_names | xargs -n 2
    echo "************************************"
    read -p "Please choose to delete from the following examples:" VMware_item
    logfilePath=${log_dire}/uninstall_${VMware_item}.log
    touch $logfilePath
    count=1
    while true; do
        if [ $count -le 3 ];then
            check_input $VMware_item
            if [ $? -ne 0 ];then
                count=`expr $count + 1`
                read -p "Please choose to delete from the following examples:" VMware_item
                continue
            fi
            break
        else
            exit 1
        fi
    done
    serviceName=${VMware_item}
}

function para_check(){
    if [ "--service"x != "$str"x -a "--all"x != "$str"x ]; then
        echo "The first parameter should be \"--service\" or \"--all\"";
        echoman
        exit 1
    fi
    if [ "--service"x = "$str"x ];then
        if [ ${#plugin_service} -eq 0 ];then
            echo "Please enter the service of the installed plugin."
            exit 1
        fi
    fi
    if [ "--all"x = "$str"x ];then
        return 5
    fi

    check_input $plugin_service
    if [ $? -ne 0 ];then
        echo "Please enter the service of the installed plugin."
        exit 1
    else
        serviceName=${plugin_service}
        logfilePath=${log_dire}/uninstall_${plugin_service}.log
        return 0
    fi
}

function prompt_message()
{
    echo -n "Are you sure you want to uninstall ? (y/n)"
    read key
    i=1
    for ((; i < 3; i++ ));do
        case $key in
        y | Y)
            return 0
            break;
            ;;
        n | N)
            exit 0
            break;
            ;;
        *)
            echo "Input invalid arguments.Are you sure you want to uninstall ? (y/n)"
            read key
            ;;
        esac
    done
    if [ $i -ge 3 ];then
        echo "input error more than three times."
        exit 1
    fi
    return 0
}
function delete_folder()
{
    log_info "start delete folder ${VMware_item}"
    sum=`grep "vmware_name" ${vmware_information_conf} | wc -l`
    if [ $sum -eq 1 ];then
        rm -rf /opt/plugin/vmware_information_tool.sh >> /dev/null 2>&1
        rm -rf /opt/plugin/conf >> /dev/null 2>&1
        userdel -f vmware
        GID=`grep plugin /etc/group | awk -F":" '{print $3}'`
        user_count=`awk -F":" '{print $4 $6}' /etc/passwd |grep ${GID} | awk -F'/' '{print $3}' | wc -l`
        if [ $user_count -eq 1 ];then
            groupdel plugin >/dev/null 2>&1
        fi
    fi
    if [ -d "/opt/plugin/${serviceName}" ];then
        rm -rf /opt/plugin/${serviceName}
    fi
    if [ -f "/usr/lib/systemd/system/${serviceName}.service" ];then
        rm -rf /usr/lib/systemd/system/${serviceName}.service
    fi
    if [ -d "/opt/plugin/cert/${serviceName}" ];then
        rm -rf /opt/plugin/cert/${serviceName}
    fi
    if [ -d "/var/log/plugin/${serviceName}" ];then
        rm -rf /var/log/plugin/${serviceName}
    fi
}

function stop_service()
{
    # retry 3 times to restart tomcat services
    service=$1
    for iter in `seq 3`;do
        systemctl stop ${service} >> /dev/null 2>&1
        if [ $? -ne 0 ];then
            continue
        fi
        return 0
    done
    return 1
}

function log_unconfig()
{
    sed -i "/plugin\/${VMware_item}/,/}/d" /etc/logrotate.d/logrotate_all >> /dev/null 2>&1
}

function del_timerOfCheckVmware_jks()
{
    if [ ! -e ${CRONTAB_FILE} ];then
        return 0
    else
        grep "${check_path}" ${CRONTAB_FILE}  > /dev/null
        if [ $? -eq 0 ];then
            sed -i 's#'$check_path'#EXCLUSIVE#;/EXCLUSIVE/d' ${CRONTAB_FILE}
        fi
    fi
}

function remove_cps() {
    ##移除cps-monitor
    cps_dir=`grep "install_path" ${vmware_information_conf} |awk -F= '{print $2}'`
    cps_installDir="${cps_dir}/cps-monitor"
    reg_command_file="${cps_installDir}/cps_monitor/reg_monitor_cli.py"
    cps_conf=${cps_dir}/etc/cps-monitor/cps/components/agent
    if [ -f $reg_command_file ];then
        ${reg_command_file} unreg ${serviceName}
        sed -i "/restart ${serviceName}/d" /etc/sudoers.d/robouser
        systemctl stop cps-monitor.service
        if [ -d ${cps_conf}/${serviceName} ];then
            rm -rf $cps_conf/${serviceName}
        fi
        systemctl start cps-monitor.service
    fi
}

function remove_info(){
    if [ -f ${vmware_information_conf} ];then
        sed -i "/${serviceName}/,/vmware_outport/d" ${vmware_information_conf}
    fi
}

function stop()
{
     stop_service $1
     systemctl daemon-reload
}

function uninstall(){
    remove_cps
    stop ${serviceName}
    delete_folder
    remove_info
    fn_log "uninstall $serviceName."
    log_unconfig
    del_timerOfCheckVmware_jks
}

# ------------ main -------------

check
check_mode
if [ $para_num -ne 0 ];then
    para_check
    if [ $? -eq 5 ];then
        prompt_message
        cps_dir=`grep "install_path" ${vmware_information_conf} |awk -F= '{print $2}'`
        cps_installDir="${cps_dir}/cps-monitor"
        reg_command_file="${cps_installDir}/cps_monitor/reg_monitor_cli.py"
        cps_conf=${cps_dir}/etc/cps-monitor/cps/components/agent
        for item in `grep "vmware_name" ${vmware_information_conf} | awk -F"=" '{print $2}'`;do
            stop ${item}
            rm -rf /usr/lib/systemd/system/${item}.service
            if [ -f $reg_command_file ];then
                ${reg_command_file} unreg ${item}
                sed -i "/restart ${item}/d" /etc/sudoers.d/robouser
            fi
        done
        ps -ef |grep /opt/plugin/vmware_plugin_|grep -v grep |awk '{print $2}'|xargs -I{} kill -9 {} >> /dev/null 2>&1
        if [ -f $reg_command_file ];then
        systemctl stop cps-monitor.service
        for item in `grep "vmware_name" ${vmware_information_conf} | awk -F"=" '{print $2}'`;do
            if [ -d ${cps_conf}/${item} ];then
                rm -rf $cps_conf/${item}
            fi
        done
        systemctl start cps-monitor.service
        fi

        userdel -f vmware
        GID=`grep plugin /etc/group | awk -F":" '{print $3}'`
        user_count=`awk -F":" '{print $4 $6}' /etc/passwd |grep ${GID} | awk -F'/' '{print $3}' | wc -l`
        if [ $user_count -eq 1 ];then
            groupdel plugin >/dev/null 2>&1
        fi

        rm -rf /opt/plugin/*
        rm -rf /var/log/plugin
        sed -i "/plugin\/vmware_plugin_/,/}/d" /etc/logrotate.d/logrotate_all >> /dev/null 2>&1
        sed -i "/vmware_plugin_/d" ${CRONTAB_FILE} >> /dev/null 2>&1
        echo "Uninstall plugin success."
        exit 0
    fi
    prompt_message
    uninstall
else
    check_mode
    if [ $? -eq 0 ];then
        show_item
    fi
    prompt_message
    uninstall
fi

echo "Uninstall plugin success."
exit 0
