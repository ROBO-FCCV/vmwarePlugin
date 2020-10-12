#!/bin/bash
current_dir=$(cd `dirname $0`; pwd)
if [[ ${current_dir} == "." ]];then
    current_dir=$PWD
fi
object_name=`grep ^install_path ${current_dir}/conf/install.conf |awk -F'/' '{print $3}'`
old_dir=/opt/plugin/
logfile_path="/var/log/plugin"
mkdir -p ${logfile_path}
touch ${logfile_path}/update_vmware_plugin.log
backup_path=/opt/robo/plugin


function writeInfo(){
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today INFO: $1." >> ${logfile_path}/update_vmware_plugin.log
}

function writeError(){
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today ERROR: $1." >> ${logfile_path}/update_vmware_plugin.log
}

function CHECK_RESULT()
{
    if [[  $? -eq 0  ]]
    then
        writeInfo "$@ succeeded."
    else
        writeError "$@ failed."
        echo -e "\033[41;37m $@ failed. \033[0m"
        exit 1
    fi
}

#去掉cp别名
unalias cp >/dev/null 2>&1

function prompt_message()
{
    echo -n "Are you sure you want to update ? (y/n)"
    read key
    i=1
    for ((; i < 4; i++ ));do
        case ${key} in
        y | Y)
            writeInfo "user input y"
            return 0
            break;
            ;;
        n | N)
            writeInfo "user input n"
            exit 0
            break;
            ;;
        *)
            writeInfo "invalid user input."
            echo "Input invalid arguments.Are you sure you want to update ? (y/n)"
            read key
            ;;
        esac
    done
    if [[ ${i} -ge 3 ]];then
        echo "input error more than three times."
        exit 1
    fi
    return 0
}


function remove_cps() {
    ##移除cps-monitor
    vmware_name=$1
    cps_installDir="/opt/${object_name}/robo/cps-monitor"
    reg_command_file="${cps_installDir}/cps_monitor/reg_monitor_cli.py"
    cps_conf=/opt/${object_name}/robo/etc/cps-monitor/cps/components/agent

    if [[ -d ${cps_conf}/${vmware_name} ]];then
        ${reg_command_file} unreg ${vmware_name}
        sed -i "/restart ${vmware_name}/d" /etc/sudoers.d/robouser
    else
        return 0
    fi
    systemctl stop cps-monitor.service
    rm -rf ${cps_conf}/${vmware_name}
    systemctl start cps-monitor.service
}

function rollback(){

    ps -ef |grep /opt/plugin/vmware_plugin_|grep -v grep >> /dev/null 2>&1
    if [[ $? -eq 0 ]];then
        ps -ef |grep /opt/plugin/vmware_plugin_|grep -v grep |awk '{print $2}'|xargs -I{} kill -9 {}
    fi
    rm -rf /opt/plugin/*
    cp -rfp ${backup_path}/ /opt/plugin/
    #删除已经升级成功的vmware_p*.service
    new_service=$(find /usr/lib/systemd/system/ -name vmware_p*.service)
    for new_file in ${new_service};
    do
        service=$(echo "${new_file}"| awk -F'/' '{print$NF}' | awk -F'.' '{print$1}')
        remove_cps ${service}
        rm -rf ${new_file}
    done
    #还原老的vmware_p*.service
    old_service=$(find ${backup_path}/ -name vmware_p*.service)
    for old_file in ${old_service};
    do
        cp -rfp ${old_file} /usr/lib/systemd/system/
    done

}

function check_update()
{
    #判断是否为省端，省端没有插件
    if [[ $(get_info.py install_scene) == "centre" ]]; then
        writeInfo "Services vmware do not need to be upgraded in centre"
        echo "Services vmware do not need to be upgraded in centre"
        exit 0
    fi
    #判断是否安装了旧插件
    if [[ ! -f ${backup_path}/conf/vmware_information.conf ]] && [[ ! -f ${old_dir}/conf/vmware_information.conf ]];then
        writeInfo "Services vmware do not install."
        echo "Services vmware do not install."
        exit 0
    fi
}

function check_version() {
    cd ${current_dir}
    rm -rf temp >> /dev/null 2>&1
    mkdir ${current_dir}/temp
    cp ${current_dir}/software/*vmware*.war temp/vmware.war
    packwar=`basename temp/vmware.war`
    cd  temp
    WarName=${packwar:0:${#packwar}-4}
    mv ${WarName}.war ${WarName}.zip
    unzip ${WarName}.zip >> /dev/null 2>&1
    version_file=`find ./ -name version.yml`
    current_version=`awk '{print $2}' ${version_file}`
    cd ${current_dir}
    rm -rf temp >> /dev/null 2>&1

    if [[ ${current_version} == ${version} ]];then
        echo "The current version is already the latest version"
        exit 0
    fi
}

function back_up() {
    mkdir -p ${backup_path}

    #备份数据
    if [[ -f ${backup_path}/vmware_information_tool.sh ]];then
        writeInfo "Services has backup"
        return 0
    fi
    cp -rfp /opt/plugin/* ${backup_path}/
    count_service=$(find /usr/lib/systemd/system/ -name vmware_p*.service)
    for service_file in ${count_service};
    do
        back_service=$(echo "${service_file}"| awk -F'/' '{print$NF}' | awk -F'.' '{print$1}')
        systemctl stop ${back_service}
        cp -rfp ${service_file} ${backup_path}/
    done

}

function copy_data() {
    cp -rfp ${backup_path}/$1/tomcat/webapps/vmware/WEB-INF/classes/vmware.yml ${old_dir}/$1/tomcat/webapps/vmware/WEB-INF/classes >> /dev/null 2>&1
    cp -rfp ${backup_path}/$1/tomcat/webapps/vmware/WEB-INF/classes/login.yml ${old_dir}/$1/tomcat/webapps/vmware/WEB-INF/classes >> /dev/null 2>&1
}

function remove_vmware()
{
    #删除插件目录
    rm -rf /opt/plugin/*
    #删除/usr/lib/systemd/system里的vmware服务脚本
    vm_service=$(find /usr/lib/systemd/system/ -name vmware_p*.service)
    for vm_file in ${vm_service};
    do
        rm -rf ${vm_file}
    done
}

function cps_check(){
    ps -ef |grep -v grep |grep /opt/${object_name}/robo/cps-monitor/cps_monitor/cps_monitor.py >> /dev/null 2>&1
    CHECK_RESULT "cps-monitor health check"
}

function upgrade() {
    prompt_message
    writeInfo "Start check cps"
    cps_check
    writeInfo "Start check update"
    check_update
    writeInfo "Finish check update"
    if [[ -f ${old_dir}vmware_information_tool.sh ]];then
        version_file=$(find ${old_dir} -name version.yml | tail -1)
    else
        version_file=$(find ${backup_path} -name version.yml | tail -1)
    fi
    version=`awk '{print $2}' ${version_file}`
    writeInfo "Start check version"
    check_version
    writeInfo "Finish check version"
    writeInfo "Start back vmware"
    back_up
    writeInfo "Finish back vmware"
    writeInfo "Start remove vmware"
    remove_vmware
    writeInfo "Finish remove vmware"
    writeInfo "Start upgrade vmware"
    #循环遍历vmware_information.conf配置文件，将老的插件的目录名称，入端口号，出端口号获取到，用于安装新插件
    plugin_count=$(cat ${backup_path}/conf/vmware_information.conf | grep vmware_name | wc -l)
    x=-1
    y=1
    for i in `seq ${plugin_count}`;do
        x=$(expr ${x} + 3)
        y=$(expr ${y} + 3)
        vmware_file=$(cat ${backup_path}/conf/vmware_information.conf | sed -n "${x},${y}p" | grep vmware_name | awk -F'=' '{print $2}')
        vmware_getport=$(cat ${backup_path}/conf/vmware_information.conf | sed -n "${x},${y}p" | grep vmware_getport | awk -F'=' '{print $2}')
        vmware_outport=$(cat ${backup_path}/conf/vmware_information.conf | sed -n "${x},${y}p" | grep vmware_outport | awk -F'=' '{print $2}')
        echo "Start upgrade ${vmware_file},please waiting....."
        sh ${current_dir}/install.sh --upgrade ajax ${vmware_getport} ${vmware_outport} ${vmware_file} >> ${logfile_path}/update_vmware_plugin.log
        if [[ $? -ne 0 ]];then
            echo "Plugin upgrade failed"
            rollback
            CHECK_RESULT "rollback"
            echo "Rollback succeeded"
            exit 0
        fi
        copy_data ${vmware_file}
        systemctl restart ${vmware_file}
        echo "Upgrade ${vmware_file} success"
    done
    writeInfo "Finish upgrade vmware"
    rm -rf /opt/robo
}

upgrade
echo "Update plugin success"
