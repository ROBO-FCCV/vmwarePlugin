#!/bin/bash
currentDir=$(cd `dirname $0`; pwd)
if [ ${currentDir} == "." ];then
    currentDir=$PWD
fi
object_name=`grep ^install_path ${currentDir}/conf/install.conf |awk -F'/' '{print $3}'`
old_dir=/opt/${object_name}/plugin/tomcat_vmware
vmware_name="vmware_plugin_001"
installDir=/opt/plugin/${vmware_name}/tomcat
logfilePath="/var/log/plugin"
mkdir -p $logfilePath
touch ${logfilePath}/update_vmware_plugin.log
backup_path=/opt/plugin/plugin_backup/data
CRONTAB_FILE=/etc/cron.d/FusionCube

function writeInfo(){
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today INFO: $1." >> ${logfilePath}/update_vmware_plugin.log
}

function writeError(){
    today=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$today ERROR: $1." >> ${logfilePath}/update_vmware_plugin.log
}

function CHECK_RESULT()
{
    if [  $? -eq 0  ]
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
        case $key in
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
    if [ $i -ge 3 ];then
        echo "input error more than three times."
        exit 1
    fi
    return 0
}

function rollback_message(){
    echo -n "Are you sure you want to rollback ? (y/n)"
    read key
    i=1
    for ((; i < 4; i++ ));do
        case $key in
        y | Y)
            writeInfo "user input y"
            return 0
            break;
            ;;
        n | N)
            writeInfo "user input n"
            exit 1
            break;
            ;;
        *)
            writeInfo "invalid user input."
            echo "Input invalid arguments.Are you sure you want to update ? (y/n)"
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

function remove_cps() {
    ##移除cps-monitor
    cps_installDir="${object_name}/cps-monitor"
    reg_command_file="${cps_installDir}/cps_monitor/reg_monitor_cli.py"
    cps_conf=${cps_dir}/etc/cps-monitor/cps/components/agent

    if [ -d ${cps_conf}/${vmware_name} ];then
        ${reg_command_file} unreg ${vmware_name}
        sed -i "/restart ${vmware_name}/d" /etc/sudoers.d/robouser
    else
        return 0
    fi
    systemctl stop cps-monitor.service
    rm -rf $cps_conf/${vmware_name}
    systemctl start cps-monitor.service
}

function rollback(){
    rollback_message
    if [ $? -eq 0 ];then
        ps -ef |grep /opt/plugin/vmware_plugin_|grep -v grep >> /dev/null 2>&1
        if [ $? -eq 0 ];then
            ps -ef |grep /opt/plugin/vmware_plugin_|grep -v grep |awk '{print $2}'|xargs -I{} kill -9 {}
        fi
        rm -rf /opt/plugin
        rm -rf /usr/lib/systemd/system/vmware_plugin_001.service
        sed -i "/plugin\/vmware_plugin_/,/}/d" /etc/logrotate.d/logrotate_all >> /dev/null 2>&1
        sed -i "/vmware_plugin_/d" ${CRONTAB_FILE} >> /dev/null 2>&1
        remove_cps
        userdel -f vmware >> /dev/null 2>&1
        systemctl start tomcat_vmware.service
    fi
}

function check_update()
{
    if [ $(get_info.py install_scene) == "centre" ]; then
        writeInfo "Services ${vmware_name} do not need to be upgraded."
        exit 0
    fi
    if [ ! -f /usr/lib/systemd/system/tomcat_vmware.service ];then
        if [ -f /usr/lib/systemd/system/vmware_plugin_001.service ];then
            echo "The current version is already the latest version"
        else
            echo "No plugins installed in the current environment,please install the plugin"
        fi
        exit 0
    fi
}

function check_version() {
    cd $currentDir
    rm -rf temp >> /dev/null 2>&1
    mkdir $currentDir/temp
    cp $currentDir/software/*vmware*.war temp/vmware.war
    packwar=`basename temp/vmware.war`
    cd  temp
    WarName=${packwar:0:${#packwar}-4}
    mv ${WarName}.war ${WarName}.zip
    unzip ${WarName}.zip >> /dev/null 2>&1
    version_file=`find ./ -name version.yml`
    current_version=`awk '{print $2}' $version_file`
    cd $currentDir
    rm -rf temp >> /dev/null 2>&1

    if [ $current_version == $version ];then
        echo "The current version is already the latest version"
        exit 0
    fi
}

function pre_upgrade() {
    mkdir -p ${backup_path}
    groupadd -f plugin
    grep vmware /etc/passwd >> /dev/null 2>&1
    if [ $? -ne 0 ];then
        useradd -g plugin -G robogrp -M -s /sbin/nologin vmware >> /dev/null 2>&1
    fi

    if [ -d $old_dir ];then
        cp -rfp ${old_dir}/webapps/vmware/WEB-INF/classes/login.yml ${backup_path}
        chown vmware:plugin ${backup_path}/login.yml
        cp -rfp ${old_dir}/webapps/vmware/WEB-INF/classes/vmware.yml ${backup_path} >> /dev/null 2>&1
        chown vmware:plugin ${backup_path}/vmware.yml >> /dev/null 2>&1
    fi
    sed -i '/^\s*$/d' ${backup_path}/vmware.yml >> /dev/null 2>&1
}

function copy_data() {
    cp -rfp ${backup_path}/vmware.yml ${installDir}/webapps/vmware/WEB-INF/classes >> /dev/null 2>&1
    cp -rfp ${backup_path}/login.yml ${installDir}/webapps/vmware/WEB-INF/classes
}

function del_old_plugin()
{
    writeInfo "start delete folder tomcat_vmware"
    if [ -d $old_dir ];then
        rm -rf $old_dir
        rm -rf /opt/${object_name}/plugin/cert/tomcat_vmware
        rm -rf /usr/lib/systemd/system/tomcat_vmware.service
    fi
    return 0
}

function stop(){
    if [ -f /usr/lib/systemd/system/tomcat_vmware.service ];then
        systemctl stop tomcat_vmware.service
        CHECK_RESULT "stop tomcat_vmware"
    fi
}

function cps_check(){
    ps -ef |grep -v grep |grep /opt/${object_name}/robo/cps-monitor/cps_monitor/cps_monitor.py >> /dev/null 2>&1
    CHECK_RESULT "cps-monitor health check"
}

function upgrade() {
    prompt_message
    cps_check
    check_update
    if [ -d $old_dir ];then
        version_file=`find $old_dir -name version.yml`
    fi
    version=`awk '{print $2}' $version_file`
    check_version
    pre_upgrade
    stop

    expect  << EOF >> ${logfilePath}/update_vmware_plugi_plugin.log
    spawn sh ${currentDir}/install.sh
    expect {
    "default*" {send "\r"}
    }
    expect eof;
    catch wait result;
    exit [lindex \$result 3]
EOF
    if [ $? -ne 0 ];then
        echo "Plugin upgrade failed"
        rollback
        CHECK_RESULT "rollback"
        echo "Rollback succeeded"
        exit 0
    fi
    copy_data
    systemctl restart vmware_plugin_001.service
    CHECK_RESULT "Update vmware_plugin"
    cd ${currentDir}
}

upgrade
del_old_plugin
echo "Update plugin success"
