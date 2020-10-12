#!/bin/bash
current_dir=$(cd `dirname $0`; pwd)
if [[ ${current_dir} == "." ]];then
    current_dir=$PWD
fi
vmware_plugin_file=/opt/plugin/conf/vmware_information.conf

for item in `grep vmware_name ${vmware_plugin_file} | awk -F"=" '{print $2}'`;do
    bash /opt/plugin/${item}/tool/replace_vmware_plugin_certs.sh
    if [[ $? -eq 0 ]];then
        echo "${item} certificate updated successfully"
    else
        echo "${item} certificate updated failed"
    fi
done

exit 0