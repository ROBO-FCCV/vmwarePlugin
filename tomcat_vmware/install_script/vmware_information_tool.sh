#!/bin/bash
currentDir=$(cd `dirname $0`; pwd)
if [[ ${currentDir} == "." ]];then
    currentDir=$PWD
fi
base_dir=/opt/plugin
options="port account"
option=$1
para_num=$#

function vcenter_list(){

    echo "*************Vcenter account list*************"
    echo ""
    cd ${currentDir}
    for item in `ls ${currentDir}`;do
        [[ -d ${item} ]] && dir=${item}
        if [[ ${dir} =~ "vmware_plugin_" ]];then
            vcenter_file=${dir}/tomcat/webapps/vmware/WEB-INF/classes/vmware.yml
            echo "-------${dir}-------"
            grep -v password ${vcenter_file} | grep -v vmwareId | more

        fi
    done
}

function port_list(){
    echo "*************Vmware_plugin information*************"
    echo ""
    cd ${currentDir}
    for item in `ls ${currentDir}`;do
        [[ -d ${item} ]] && dir=${item}
        if [[ ${dir} =~ "vmware_plugin_" ]];then
            vmware_plugin_file=${dir}/conf/install.conf
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