#!/bin/bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

currentDir=$(cd `dirname $0`; pwd)
if [[ ${currentDir} == "." ]];then
    currentDir=$PWD
fi
check_dir=${currentDir}/..
base_dir=/opt/plugin
options="port account"
option=$1
para_num=$#

function vcenter_list(){

    echo "*************Vcenter account list*************"
    echo ""
    cd ${check_dir}/
    for item in `ls ${check_dir}`;do
        [[ -d ${item} ]] && dir=${item}
        if [[ ${dir} =~ "vmware_plugin_" ]];then
            vcenter_file=${dir}/tomcat/webapps/vmware/WEB-INF/classes/vmware.yml
            check_link_current_path ${vcenter_file}
            if [[ $? -ne 0 ]]; then
                log_error "${vcenter_file} contains link"
                echo -e "\033[41;37m ${vcenter_file} contains link. \033[0m"
                exit 1
            fi
            echo "-------${dir}-------"
            grep -v password ${vcenter_file} | grep -v vmwareId | more

        fi
    done
}

function port_list(){
    echo "*************Vmware_plugin information*************"
    echo ""
    cd ${check_dir}
    for item in `ls ${check_dir}`;do
        [[ -d ${item} ]] && dir=${item}
        if [[ ${dir} =~ "vmware_plugin_" ]];then
            vmware_plugin_file=${dir}/conf/install.conf
            check_link_current_path ${vmware_plugin_file}
            if [[ $? -ne 0 ]]; then
                log_error "${vmware_plugin_file} contains link"
                echo -e "\033[41;37m ${vmware_plugin_file} contains link. \033[0m"
                exit 1
            fi
            vmware_name=`grep vmware_name ${vmware_plugin_file} | awk -F"=" '{print $2}'`
            vmware_getport=`grep vmware_getport ${vmware_plugin_file} | awk -F"=" '{print $2}'`
            echo "${vmware_name}:${vmware_getport}"
        fi
    done
}

function check_para(){
    if [[ ! ${options} =~ $1  ]];then
        echo "usage: sh information_tool.sh {port|account} "
        exit 1
    elif [[ ${para_num} -ne 1 ]];then
        echo "usage: sh information_tool.sh {port|account} "
        exit 1
    fi
}

check_para ${option}
case $1 in
    port)
    port_list
    ;;
    account)
    vcenter_list
    ;;
esac

exit 0
