#! /bin/bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

current_dir=$(dirname $0)
if [[ ${current_dir} == "." ]];then
    current_dir=$PWD
fi
source ./utils.sh
logfilepath="/var/log/plugin"
install_dir_plugin=/opt/plugin/${vmware_name}/tomcat
temp_path=${install_dir_plugin}/temp
local_ip=`python /usr/bin/get_info.py manage_ip`
top_dir=$(dirname $(cd `dirname $0`; pwd))

cd ../
install_cur_dir=`pwd`
obj_name=`grep ^install_path ${install_cur_dir}/conf/install.conf |awk -F'/' '{print $3}'`

mkdir -p ${logfilepath}/${vmware_name}
mkdir -p ${temp_path}
touch ${logfilepath}/install_${vmware_name}.log

function check_para (){
    ls ${top_dir}/software/*vmware*.war >> /dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        echo -e "\033[41;37m The plugin_war was not found, please re-execute the recovery and enter the correct resource path ! \033[0m"
        log_error "Compressed package format error"
        exit 0;
    fi
}

function config_tomcat(){

    cd ${install_dir_plugin}/webapps
    packName=$1
    num=`sed -n  -e '/<\/Host>/='  ../conf/server.xml`
    sed -i  $((num-1))"s|$|\n<Context path=\"/${packName}\" reloadable=\"false\" docBase=\"${packName}\" />|" ../conf/server.xml
    sed -i '/docBase="ROOT"/d' ${install_dir_plugin}/conf/server.xml
    sed -i "s#\/deploy#\/#g" ${install_dir_plugin}/conf/server.xml
    sed -i "s/vmware_plugin/${vmware_name}/g" ${install_dir_plugin}/conf/server.xml
    sed -i "s/19091/${vmware_getport}/g" ${install_dir_plugin}/conf/server.xml
    sed -i "s/19091/${vmware_getport}/g" ${install_dir_plugin}/bin/setenv.sh
    sed -i "s/19001/${vmware_outport}/g" ${install_dir_plugin}/conf/server.xml
    dos2unix ${install_dir_plugin}/conf/server.xml >> /dev/null 2>&1
    local start_file=${install_dir_plugin}/bin/startup.sh
    sed -i -c '/export LD_LIBRARY_PATH=/d' ${start_file}
    line_num=$(sed -n -e '/EXECUTABLE=catalina.sh/=' ${start_file})
    sed -i ${line_num}"a export LD_LIBRARY_PATH=/opt/${obj_name}/robo/kmc/lib"  ${start_file}
}

function del_dir(){
    rm -rf ${temp_path}
}

function install_plugin(){
    rm -rf ${temp_path}/*
    cp ${top_dir}/software/*vmware*.war ${temp_path}/vmware.war
    packwar=`basename ${temp_path}/vmware.war`
    cd  ${temp_path}
    WarName=${packwar:0:${#packwar}-4}
    mv ${WarName}.war ${WarName}.zip

    cp -f ${temp_path}/${WarName}.zip ${install_dir_plugin}/webapps/${WarName}.zip
    cd ${install_dir_plugin}/webapps/
    unzip -o -d ${WarName} ${WarName}.zip >> /dev/null 2>&1
    fn_log "Decompression"
    #配置Tomcat
    config_tomcat ${WarName}
    chown -R vmware:plugin  ${install_dir_plugin}/webapps/${WarName}
    chmod -R 700 ${WarName}

    rm -f ${WarName}.zip
    rm -f ${war}
    del_dir
    touch /opt/plugin/${vmware_name}/tomcat/webapps/vmware/WEB-INF/classes/vmware.yml
    chown vmware:plugin /opt/plugin/${vmware_name}/tomcat/webapps/vmware/WEB-INF/classes/vmware.yml
    chmod 700 /opt/plugin/${vmware_name}/tomcat/webapps/vmware/WEB-INF/classes/vmware.yml
    sed -i "s/tomcat_https/${vmware_name}/g" /opt/plugin/${vmware_name}/tomcat/webapps/vmware/WEB-INF/classes/log4j2.xml
    sed -i "s/tomcat_https/${vmware_name}/g" /opt/plugin/${vmware_name}/tomcat/webapps/vmware/WEB-INF/classes/application.yml
}

function install(){
    sys=`cat /etc/system-release | grep release | awk '{print $1}'`
    if [[ $sys = EulerOS ]] && [[ `uname -r` =~ x86 ]];then
        install_plugin
    elif [[ $sys = EulerOS ]] && [[ `uname -r` =~ aarch64 ]];then
        install_plugin
    elif [[ $sys = Red ]] && [[ `uname -r` =~ x86 ]];then
        pass
    elif [[ $sys = CentOS ]] && [[ `uname -r` =~ x86 ]];then
        pass
    fi
}

check_para
install
systemctl daemon-reload
systemctl restart ${vmware_name}.service
ps -ef |grep ${vmware_name} |grep -v grep >> /dev/null 2>&1
fn_log "Installing the plugin"
