#!/bin/bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

current_dir=$(cd `dirname $0`; pwd)
if [[ ${current_dir} == "." ]];then
    current_dir=$PWD
fi
mask_str=`grep ^install_path ${current_dir}/conf/install.conf |awk -F'/' '{print $3}'`
base_dir=/opt/plugin
install_conf=${current_dir}/conf/install.conf
instant_sum=100
str=$1
input_put=$2
getport=$3
outport=$4
vmware_plugin=$5
para_num=$#

function echoman () {
echo "Usage: sh install.sh [--port <port>]
Default available port number range: 18501~18600"
}

function para_check(){
    if [[ "--upgrade"x == "$str"x ]]; then
        export vmware_getport=${getport}
        export vmware_outport=${outport}
        export vmware_name="${vmware_plugin}"
        return 0
    fi

    if [[ "--port"x != "$str"x ]]; then
        echo "The first parameter should be \"--port\"";
        echoman
        exit 1
    fi
    echo ${input_put} | grep -E --color '^(.*[0-9]).*$' >> /dev/null 2>&1
    if [[ $? -ne 0 ]];then
        echo "Please enter the correct port format"
        echoman
        exit 1
    fi

    check_port ${input_put}
    if [[ $? -ne 0 ]];then
        echo -e "\033[41;37m the port: ${input_put} is in used, please check! \033[0m"
        exit 1
    fi
    if [[ ! `seq 18501 18600` =~ $input_put ]];then
        echo "Please enter a value in the range:{18501 18600}."
        exit 1
    fi
    export vmware_getport=${input_put}
    out_port
}

function add_user(){

    grep vmware /etc/passwd >> /dev/null 2>&1
    if [[ $? -ne 0 ]];then
        groupadd -f plugin
        useradd -g plugin -G robogrp -M -s /sbin/nologin vmware >> /dev/null 2>&1
    fi
}

function Interaction(){
    if [[ ! -d ${base_dir} ]];then
        mkdir -p ${base_dir}
    fi
    instant_num=1
    for item in `ls ${base_dir}`;do
        if [[ ${item} =~ "vmware_plugin_" ]];then
            instant_num=`expr ${instant_num} + 1`
        fi
    done
    if [[ ${instant_num} -gt ${instant_sum} ]];then
        echo "Vmware_plugin instance is out of maximum range."
        exit 1
    fi
    count_1=1
    while true; do
        if [[ ${count_1} -le 3 ]]; then
            get_port "manu"
            read -p "Please enter the port number(18501-18600) to install(default ${port_temp})：" Port
            str_length=${#Port}
            if [[ ${str_length} -ne 0 ]];then
                echo ${Port} | grep -E --color '^(.*[0-9]).*$' >> /dev/null 2>&1
                if [[ $? -ne 0 ]];then
                    count_1=`expr ${count_1} + 1`
                    continue
                fi
                if [[ ! `seq 18501 18600` =~ $Port ]];then
                    echo "Please enter a value in the range:{18501 18600}."
                    continue
                fi
                check_port ${Port}
                if [[ $? -ne 0 ]];then
                    echo -e "\033[41;37m the port: ${Port} is in used, please check! \033[0m"
                    count_1=`expr ${count_1} + 1`
                    continue
                fi
                export vmware_getport=${Port}
                out_port
                break
            else
                get_port
                out_port
                break
            fi
        else
            log_error "Install vmware_plugin fail."
            echo "Install vmware_plugin fail!"
            exit 1
        fi
    done
}

function get_port(){
    for item in `seq 18501 18600`;do
        check_port ${item}
        if [[ $? -ne 0 ]];then
            continue
        fi
        if [[ $1 =~ "manu" ]];then
            port_temp=${item}
        else
            export vmware_getport=${item}
        fi
        break
    done
}

function out_port(){
    for item in `seq 28501 28700`;do
        check_port ${item}
        if [[ $? -ne 0 ]];then
            continue
        fi
        export vmware_outport=${item}
        break
    done
}

function num_trs(){
    number=$1
    if [[ ${#number} -eq 1 ]];then
        number_trs="00${number}"
    fi
    if [[ ${#number} -eq 2 ]];then
        number_trs="0${number}"
    fi
    if [[ ${#number} -eq 3 ]];then
        number_trs=${number}
    fi
}

function get_name(){
    for num in `seq 100`;do
        num_trs ${num}
        mkdir -p ${base_dir}
        cd ${base_dir}
        dir_str=`ls ./`
        if [[ ! ${dir_str} =~ $number_trs ]];then
            export vmware_name="vmware_plugin_${number_trs}"
            break
        fi
    done
}

function check_port() {
    port=$1
    netstat -tunlp |grep ${port} >/dev/null 2>&1
    if [[ $? -eq 0 ]];then
        return 1
    fi
    if [[ -f ${base_dir}/conf/vmware_information.conf ]];then
        grep ${port} ${base_dir}/conf/vmware_information.conf >/dev/null 2>&1
        if [[ $? -eq 0 ]];then
            return 1
        fi
    fi
}
function pre_install(){
    rm -rf ${current_dir}/temp >> /dev/null 2>&1
    mkdir -p ${current_dir}/temp
    cp -r ${current_dir}/install_script/* ${current_dir}/temp
    #替换字段

    for file in `grep -w -i object_name ${current_dir}/temp -R |grep -v Binary |grep -v install.conf|awk -F: '{print $1}'|uniq`;do
        sed -i "s/object_name/${mask_str}/g" ${file}
    done
    sed -i "s/vmware_plugin/${vmware_name}/g" ${current_dir}/temp/utils.sh
}
function check(){
    if ! echo `whoami` | grep -q '^root$'; then
        echo -e "\033[41;37m "Please use the root user to perform plugin operations." \033[0m"
        exit 0;
    fi
    cpu_check
    mem_check
    disk_check
}

function cpu_check(){

    TIME_INTERVAL=2
    LATEST_CPU_INFO=$(cat /proc/stat | grep -w cpu | awk '{print $2,$3,$4,$5,$6,$7,$8}')
    LATEST_TOTAL_CPU_T=$(echo ${LATEST_CPU_INFO} | awk '{print $1+$2+$3+$4+$5+$6+$7}')
    LATEST_CPU_USAGE=$(echo ${LATEST_CPU_INFO} | awk '{print $1+$2+$3}')
    sleep ${TIME_INTERVAL}
    NEXT_CPU_INFO=$(cat /proc/stat | grep -w cpu | awk '{print $2,$3,$4,$5,$6,$7,$8}')
    NEXT_TOTAL_CPU_T=$(echo ${NEXT_CPU_INFO} | awk '{print $1+$2+$3+$4+$5+$6+$7}')
    NExT_CPU_USAGE=$(echo ${NEXT_CPU_INFO} | awk '{print $1+$2+$3}')
    #用户+系统+nice时间
    TOTAL_BUSY=$(echo ${NExT_CPU_USAGE} ${LATEST_CPU_USAGE} | awk '{print $1-$2}')
    #CPU总时间
    TOTAL_TIME=$(echo ${NEXT_TOTAL_CPU_T} ${LATEST_TOTAL_CPU_T} | awk '{print $1-$2}')
    #CPU总时间百分比
    CPU_USAGE=$(echo ${TOTAL_BUSY} ${TOTAL_TIME} | awk '{printf "%.2f", $1/$2*100}')
    if [[ `expr ${CPU_USAGE} \> 95` -eq 1 ]];then
        echo "CPU usage is too high，install vmware_plugin fail!"
        exit 1
    fi
}

function mem_check(){
    mem_total=$(free -m | awk -F '[ :]+' 'NR==2{print $2}')
    mem_used=$(free -m | awk -F '[ :]+' 'NR==2{print $3}')
    #统计内存使用率
    mem_used=$(awk 'BEGIN{printf "%.0f\n",('${mem_used}'/'${mem_total}')*100}')
    if [[ `expr ${mem_used} \> 95` -eq 1 ]];then
        echo "Mem usage is too high，install vmware_plugin fail!"
        exit 1
    fi
}

function disk_check(){
    free_disk=`df -m /opt | tail -n 1 |grep -v Filesystem |awk '{print $4}'`
    if [[ ${free_disk} -lt 1024 ]];then
        echo "Not enough disk space for /opt，install vmware_plugin fail!"
        exit 1
    fi
}

function configur_add(){
    if [[ ! -d ${base_dir}/conf ]];then
        mkdir -p ${base_dir}/conf
    fi
    if [[ ! -f ${base_dir}/conf ]];then
        touch ${base_dir}/conf/vmware_information.conf
        chmod 600 ${base_dir}/conf/vmware_information.conf
    fi
    install_path=`grep "install_path" ${install_conf} | awk -F"=" '{print $2}'`
    grep "install_path" ${base_dir}/conf/vmware_information.conf >/dev/null 2>&1
    if [[ $? -ne 0 ]];then
        echo "install_path=${install_path}" >> ${base_dir}/conf/vmware_information.conf
    fi
    echo "vmware_name=${vmware_name}" >> ${base_dir}/conf/vmware_information.conf
    echo "  vmware_getport=${vmware_getport}" >> ${base_dir}/conf/vmware_information.conf
    echo "  vmware_outport=${vmware_outport}" >> ${base_dir}/conf/vmware_information.conf
}

function install(){
    echo "Start installing plugin"
    echo "waiting....."
    add_user
    if [[ "--upgrade"x != "$str"x ]]; then
        get_name
    fi
    pre_install
    configur_add
    cd ${current_dir}/temp
    source ./utils.sh
    bash ./setup_tomcat.sh
    fn_log "setup_tomcat.sh"
    bash ./setup_plugin.sh
    fn_log "setup_plugin.sh"
    cd ${current_dir}
    rm -rf ${current_dir}/temp
}

function check_service(){
    sleep 1
    ps -ef |grep ${vmware_name} |grep -v grep >> /dev/null 2>&1
    fn_log "Installing the plugin"
    for item_time in `seq 30`;do
        curl -k -s https://127.0.0.1:${vmware_getport}/vmware/version >> /dev/null 2>&1
        if [[ $? -ne 0 ]];then
            sleep 2
            continue
        else
            return 0
        fi
    done
    return 1
}

check
if [[ ${para_num} -ne 0 ]];then
    para_check
    install
else
    Interaction
    install
fi
check_service
fn_log "Installing the plugin"
echo "Installing the plugin succeeded."
exit 0
