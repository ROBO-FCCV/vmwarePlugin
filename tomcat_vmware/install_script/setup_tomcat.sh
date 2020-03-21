#/bin/bash

#获取环境路径信息
currentDir=$(dirname $0)
if [ ${currentDir} == "." ];then
    currentDir=$PWD
fi
cd ../
installCurDir=`pwd`
installPackageDir=$installCurDir/software
installDir=/opt/plugin/${vmware_name}/tomcat
mainDire=/opt/plugin/${vmware_name}
tool_file=/opt/plugin/${vmware_name}/tool
conf_file=/opt/plugin/${vmware_name}/conf
installTempDir=/opt/object_name/tomcat_temp
cert_path=/opt/plugin/cert/${vmware_name}
check_path=${mainDire}/sbin/vmware_cert_check.sh
CRONTAB_FILE=/etc/cron.d/FusionCube
base_dir=`grep "install_path" ${installCurDir}/conf/install.conf |awk -F= '{print $2}'`
cps_installDir="${base_dir}/cps-monitor"
reg_command_file="${cps_installDir}/cps_monitor/reg_monitor_cli.py"
logrotate_file=/etc/logrotate.d/logrotate_all
logrotate_path=/var/log/plugin/${vmware_name}
source $currentDir/utils.sh

function createDir()
{
    #创建安装文件夹
    rm -rf ${installDir}
    mkdir -p ${installDir}
    rm -rf ${installTempDir}
    mkdir -p ${installTempDir}
    rm -rf ${cert_path}
    mkdir -p ${cert_path}/temp
    rm -rf ${tool_file}
    mkdir -p ${tool_file}
    mkdir -p ${mainDire}/sbin
    mkdir -p ${conf_file}
}

function gen_cert()
{
    cd ${mainDire}/sbin
    source ${mainDire}/sbin/cert_gen.sh
    service_name=${vmware_name}
    gen_tomcat_plugin_cert $service_name ${cert_path}/temp
    if [ $? -ne 0 ];then
        echo "gen_server_cert failed"
        exit 1
    fi
    if [ -f ${cert_path}/temp/*keystore.jks ];then
        cp $cert_path/temp/*keystore.jks $cert_path/keystore.jks
        chmod 600 $cert_path/keystore.jks
        chown vmware:plugin -R $cert_path
        rm -rf ${cert_path}/temp
    else
        echo "create ${service_name} cert failed"
        exit 1
    fi
    cd ${installCurDir}
}

function add_timerOfCheckVmware_jks()
{
    if [ ! -e ${CRONTAB_FILE} ];then
        touch ${CRONTAB_FILE}
    fi
    grep "${check_path}" ${CRONTAB_FILE}
    if [ $? -ne 0 ];then
        echo "0 0 * * * root sh ${check_path} " >> ${CRONTAB_FILE}
    fi
    grep -w "/var/log/plugin" ${CRONTAB_FILE} >> /dev/bull 2>&1
    if [ $? -ne 0 ];then
        echo "*/10 * * * * root find /var/log/plugin -name \"*.gz\"  -print0  | xargs -0 chmod 400 " >> ${CRONTAB_FILE}
    fi
}

function extractTempateTomcat()
{
    #解压tomcat安装文件到指定路径
    tar -zxvf $installPackageDir/*tomcat*.tar.gz -C $installTempDir > /dev/bull 2>&1
    cd $installTempDir
    #解压替换tomcat下基础配置
    #拷贝 lib 文件到指定的安装路径下
    for dir in $(ls ./)
    do
        [ -d $dir ] && packageDirName=$dir
    done

    mv $packageDirName tomcat/
    cp $currentDir/encryptKeystorePass.* $installTempDir/tomcat/lib/
    cp $currentDir/*.jar $installTempDir/tomcat/lib/
    rm -rf  $installTempDir/tomcat/webapps/docs
    rm -rf  $installTempDir/tomcat/webapps/examples
    rm -rf  $installTempDir/tomcat/webapps/host-manager
    rm -rf  $installTempDir/tomcat/webapps/manager
    rm -rf  $installTempDir/tomcat/webapps/ROOT/WEB-INF/
    rm -rf  $installTempDir/tomcat/webapps/ROOT/*.png
    rm -rf  $installTempDir/tomcat/webapps/ROOT/*.svg
    rm -rf  $installTempDir/tomcat/webapps/ROOT/*.ico
    rm -rf  $installTempDir/tomcat/webapps/ROOT/*.jsp
    rm -rf  $installTempDir/tomcat/webapps/ROOT/*.txt
    rm -rf  $installTempDir/tomcat/webapps/ROOT/*.css
    rm -rf  $installTempDir/tomcat/webapps/ROOT/*.gif
    mkdir -p $installTempDir/tomcat/work/Catalina/localhost/ROOT/
    cp $currentDir/conf/configTemplate/catalina.policy $installTempDir/tomcat/conf/
    cp $currentDir/conf/configTemplate/context.xml $installTempDir/tomcat/conf/
    cp $currentDir/conf/configTemplate/jaspic-providers.xsd $installTempDir/tomcat/conf/
    cp $currentDir/conf/configTemplate/tomcat-users.xml $installTempDir/tomcat/conf/
    cp $currentDir/conf/configTemplate/web.xml $installTempDir/tomcat/conf/
    cp $currentDir/conf/configTemplate/setenv.sh $installTempDir/tomcat/bin/
    sed -i "s/vmware_plugin/${vmware_name}/g" $installTempDir/tomcat/bin/setenv.sh
    cp $currentDir/conf/configTemplate/catalina.sh  $installTempDir/tomcat/bin/catalina.sh
}

function installTomcat()
{
    cd $currentDir
    cp -r $installTempDir/tomcat/* $installDir/

    ######修改配置
    cp $currentDir/conf/configTemplate/server.xml $installDir/conf/server.xml
    cp $currentDir/conf/configTemplate/catalina.properties $installDir/conf/catalina.properties
    cp $currentDir/conf/configTemplate/vmware_plugin.service /usr/lib/systemd/system/${vmware_name}.service
    sed -i "s/vmware_plugin/${vmware_name}/g" /usr/lib/systemd/system/${vmware_name}.service
    ######修改权限
    chown -R vmware:plugin $mainDire
    chmod 755 /opt/plugin
    chmod 755 /opt/plugin/cert
    chmod -R 700 ${installDir}
    chmod 600 ${installDir}/bin/*
    chmod 700 ${installDir}/bin/*.sh
    chmod 600 ${installDir}/conf/*
    chmod 700 ${installDir}/logs
    chmod 700 ${installDir}/webapps

    sed -i "s/CATALINA_OUT=/#&/g" ${installDir}/bin/catalina.sh
    sed -i "/CATALINA_OUT=/a\  CATALINA_OUT=/var/log/plugin/${vmware_name}/catalina.out" ${installDir}/bin/catalina.sh
    sed -i "s#\${catalina.base}/logs#/var/log/plugin/${vmware_name}#g" ${installDir}/conf/logging.properties
    sed -i "/^2localhost.org/ s/FINE/OFF/g" ${installDir}/conf/logging.properties
    sed -i "/^3manager.org/ s/FINE/OFF/g" ${installDir}/conf/logging.properties
    sed -i "/^4host-manager.org/ s/FINE/OFF/g" ${installDir}/conf/logging.properties
    mkdir -p /var/log/plugin/${vmware_name}
    chmod 755 /var/log/plugin
    chmod 750 /var/log/plugin/${vmware_name}
    chown vmware:plugin /var/log/plugin/${vmware_name}
}
function deletetemdir()
{
    rm -rf $installTempDir
}

function add_file()
{
    if [ ! -d /opt/plugin/sbin ];then
        mkdir -p /opt/plugin/sbin
    fi
    if [ ! -f /opt/plugin/sbin/registry_cps.sh ];then
        cp $currentDir/registry_cps.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/registry_cps.sh
    fi
    if [ ! -f /opt/plugin/sbin/replace_certs.sh ];then
        cp $currentDir/replace_certs.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/replace_certs.sh
    fi

    cp $currentDir/vmware_tool.py $tool_file
    cp $installCurDir/uninstall.sh ${mainDire}/sbin
    cp $currentDir/vmware_pass_update.py $tool_file
    cp $currentDir/replace_vmware_plugin_certs.sh $tool_file
    if [ ! -f /opt/plugin/vmware_information_tool.sh ];then
        cp $currentDir/vmware_information_tool.sh /opt/plugin
        chmod 500 /opt/plugin/vmware_information_tool.sh
    fi
    chmod 500 $tool_file/*
    chown root:root -R $tool_file
    cp $currentDir/cert_gen.sh ${mainDire}/sbin
    cp $currentDir/vmware_cert_check.sh ${mainDire}/sbin

    for item in `ls ${tool_file}`;do
        sed -i "s/vmware_plugin/${vmware_name}/g" ${tool_file}/${item}
        sed -i "s/19091/${vmware_getport}/g" ${tool_file}/${item}
    done
    for item in `ls ${mainDire}/sbin`;do
        sed -i "s/vmware_plugin/${vmware_name}/g" ${mainDire}/sbin/${item}
    done

    chmod 500 ${mainDire}/sbin/*
    chown root:root -R ${mainDire}/sbin

    cp ${installCurDir}/conf/install.conf $conf_file
    echo "" >> ${conf_file}/install.conf
    echo "vmware_name=${vmware_name}" >> ${conf_file}/install.conf
    echo "vmware_getport=${vmware_getport}" >> ${conf_file}/install.conf
    echo "vmware_outport=${vmware_outport}" >> ${conf_file}/install.conf
}

function cps_check(){
    ps -ef |grep -v grep |grep /opt/object_name/robo/cps-monitor/cps_monitor/cps_monitor.py >> /dev/null 2>&1
    fn_log "cps-monitor health check"
}

function add_cps() {
##加入cps-monitor
    ${reg_command_file} reg ${vmware_name} --type 1 --cmd "/opt/plugin/${vmware_name}" --proc-num 1 --heartbeat-timeout 20 --heartbeat-interval 15 --no-control  --restart-script "sudo /bin/systemctl restart ${vmware_name}"
    echo "robouser ALL=(root) NOPASSWD: /bin/systemctl restart ${vmware_name}" >> /etc/sudoers.d/robouser
    echo "" >> /etc/sudoers.d/robouser
}

function log_config(){
    cat << EOF >> $logrotate_file
$logrotate_path/* {
    missingok
    rotate 10
    compress
    size=10M
    copytruncate
    create 0640 tomcat robogrp
}
EOF
}

function auto_start(){
    systemctl stop ${vmware_name}.service
    systemctl enable ${vmware_name}.service
}

cps_check
createDir
add_file
gen_cert
add_timerOfCheckVmware_jks >> /dev/null 2>&1
extractTempateTomcat
installTomcat
deletetemdir
auto_start >> /var/log/plugin/${vmware_name}/install_plugin.log 2>&1
add_cps
log_config


