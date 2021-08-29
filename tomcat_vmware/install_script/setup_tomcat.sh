#
# Copyright (c). 2021-2021. All rights reserved.
#

#/bin/bash

#获取环境路径信息
current_dir=$(dirname $0)
if [[ ${current_dir} == "." ]];then
    current_dir=$PWD
fi
cd ../
install_cur_dir=`pwd`
install_package_dir=${install_cur_dir}/software
install_dir=/opt/plugin/${vmware_name}/tomcat
main_dire=/opt/plugin/${vmware_name}
tool_file=/opt/plugin/${vmware_name}/tool
conf_file=/opt/plugin/${vmware_name}/conf
obj_name=`grep ^install_path ${install_cur_dir}/conf/install.conf |awk -F'/' '{print $3}'`
install_temp_dir=/opt/${obj_name}/tomcat_temp
cert_path=/opt/plugin/cert/${vmware_name}
check_path=${main_dire}/sbin/vmware_cert_check.sh
crontab_file=/etc/cron.d/FusionCube
base_dir=`grep "install_path" ${install_cur_dir}/conf/install.conf |awk -F= '{print $2}'`
cps_installdir="${base_dir}/cps-monitor"
reg_command_file="${cps_installdir}/cps_monitor/reg_monitor_cli.py"
logrotate_file=/etc/logrotate.d/logrotate_vmware
logrotate_path=/var/log/plugin/${vmware_name}
source ${current_dir}/utils.sh

function createDir()
{
    #创建安装文件夹
    rm -rf ${install_dir}
    mkdir -p ${install_dir}
    rm -rf ${install_temp_dir}
    mkdir -p ${install_temp_dir}
    rm -rf ${cert_path}
    mkdir -p ${cert_path}/temp
    rm -rf ${tool_file}
    mkdir -p ${tool_file}
    mkdir -p ${main_dire}/sbin
    mkdir -p ${conf_file}
}

function gen_cert()
{
    cd ${main_dire}/sbin
    source ${main_dire}/sbin/cert_gen.sh
    service_name=${vmware_name}
    config_store_password ${cert_path}/${service_name}.conf
    gen_tomcat_plugin_cert ${service_name} ${cert_path}/temp
    if [[ $? -ne 0 ]];then
        echo "gen_server_cert failed"
        exit 1
    fi
    if [ -f ${cert_path}/temp/*keystore.jks ];then
        cp ${cert_path}/temp/*keystore.jks ${cert_path}/keystore.jks
        chmod 600 ${cert_path}/keystore.jks
        chown vmware:plugin -R ${cert_path}
        rm -rf ${cert_path}/temp
    else
        echo "create ${service_name} cert failed"
        exit 1
    fi
    cd ${install_cur_dir}
}

function add_timerOfCheckVmware_jks()
{
    if [[ ! -e ${crontab_file} ]];then
        touch ${crontab_file}
    fi
    grep "${check_path}" ${crontab_file}
    if [[ $? -ne 0 ]];then
        echo "0 0 * * * root sh ${check_path} " >> ${crontab_file}
    fi
    grep -w "/var/log/plugin" ${crontab_file} >> /dev/bull 2>&1
    if [[ $? -ne 0 ]];then
        echo "*/10 * * * * root find /var/log/plugin -name \"*.gz\"  -print0  | xargs -0 chmod 440 " >> ${crontab_file}
    fi
}

function extractTempateTomcat()
{
    #解压tomcat安装文件到指定路径
    tar -zxvf ${install_package_dir}/*tomcat*.tar.gz -C ${install_temp_dir} > /dev/bull 2>&1
    cd ${install_temp_dir}
    #解压替换tomcat下基础配置
    #拷贝 lib 文件到指定的安装路径下
    for dir in $(ls ./)
    do
        [[ -d ${dir} ]] && packageDirName=${dir}
    done

    mv ${packageDirName} tomcat/
    cp ${current_dir}/encryptKeystorePass.* ${install_temp_dir}/tomcat/lib/
    cp ${current_dir}/*.jar ${install_temp_dir}/tomcat/lib/
    rm -rf  ${install_temp_dir}/tomcat/webapps/docs
    rm -rf  ${install_temp_dir}/tomcat/webapps/examples
    rm -rf  ${install_temp_dir}/tomcat/webapps/host-manager
    rm -rf  ${install_temp_dir}/tomcat/webapps/manager
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/WEB-INF/
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/*.png
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/*.svg
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/*.ico
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/*.jsp
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/*.txt
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/*.css
    rm -rf  ${install_temp_dir}/tomcat/webapps/ROOT/*.gif
    mkdir -p ${install_temp_dir}/tomcat/work/Catalina/localhost/ROOT/
    cp ${current_dir}/conf/configTemplate/context.xml ${install_temp_dir}/tomcat/conf/
    cp ${current_dir}/conf/configTemplate/jaspic-providers.xsd ${install_temp_dir}/tomcat/conf/
    cp ${current_dir}/conf/configTemplate/setenv.sh ${install_temp_dir}/tomcat/bin/
    sed -i "s/vmware_plugin/${vmware_name}/g" ${install_temp_dir}/tomcat/bin/setenv.sh
    #写入web.xml的特殊配置
    sed -i "\#</web-app>#d" ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <security-constraint>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <web-resource-collection>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "            <url-pattern>/*</url-pattern>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "            <http-method>OPTIONS</http-method>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "            <http-method>HEAD</http-method>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "            <http-method>TRACE</http-method>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        </web-resource-collection>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <auth-constraint>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        </auth-constraint>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </security-constraint>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>404</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>403</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "       <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>400</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "   </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>402</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>401</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>405</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>406</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>407</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "       <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>413</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>414</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>501</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <error-code>500</error-code>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    <error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <exception-type>java.lang.Throwable</exception-type>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "        <location>/error.html</location>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "    </error-page>" >> ${install_temp_dir}/tomcat/conf/web.xml
    echo " " >> ${install_temp_dir}/tomcat/conf/web.xml
    echo "</web-app>" >> ${install_temp_dir}/tomcat/conf/web.xml
    #写入catalina.policy的特殊配置
    echo " " >> ${install_temp_dir}/tomcat/conf/catalina.policy
    echo "grant {
permission java.security.AllPermission;
};" >> ${install_temp_dir}/tomcat/conf/catalina.policy
}

function change_keystore_password()
{
    local security_cfg_file=${cert_path}/${service_name}.conf
    local en_pass=`grep "^truststore=" ${security_cfg_file}|awk -F "=" '{print $2}'`
    if [[ $? -ne 0 ]]; then
       log_error "Get keystore password fail." 
       exit 1
    fi
    local de_pass=`python -c "import kmc.kmc;import os;os.environ['KMC_DATA_USER']='vmware';print(kmc.kmc.API().decrypt(0, '${en_pass}'))"`
    if [[ $? -ne 0 ]]; then
       log_error "Decrypt keystore password fail." 
       exit 1
    fi
    log_info "Decrypt keystore password successfully." 
    cd ${install_dir}/lib
    expect > ${current_dir}/temp_pass.info << EOF
spawn bash encryptKeystorePass.sh
expect {
"keyStorePass:" {send "${de_pass}\r";exp_continue}
}
catch wait result;
puts \$result;
exit [lindex \$result 3]
expect eof;
EOF
    if [[ $? -ne 0 ]]; then
        log_error  "Get tomccat keystore password file fail." 
        exit 1
    fi
    local key=`grep "keystorePass=" ${current_dir}/temp_pass.info`
    if [[ $? -ne 0 ]]; then
        rm -rf  ${current_dir}/temp_pass.info
        log_error  "Get keystorePass error" 
        exit 1
    fi
    tomcat_key=${key#*=}
    log_info  "Get keystorePass successfully" 
    local key_material=`grep "tomcat.connector.https.keyMaterial=" ${current_dir}/temp_pass.info`
    if [[ $? -ne 0 ]]; then
        rm -rf ${current_dir}/temp_pass.info
        log_error  "Get keyMaterial error" 
        exit 1
    fi
    tomcat_key_material=${key_material#*=}
    rm -rf ${current_dir}/temp_pass.info
    log_info "Get keyMaterial successfully"
    local shutdown_command=$(mkpasswd -l 20 -s 0)
    sed -i "s/server_shutdown_command/${shutdown_command}/g"  ${install_dir}/conf/server.xml
    sed -i "/keystorePass=/c keystorePass=\"${tomcat_key}\"" ${install_dir}/conf/server.xml
    sed -i s#maxPostSize\=\"53687091200\"#maxPostSize\=\"1024\"#g ${install_dir}/conf/server.xml
    sed -i "/tomcat.connector.https.keyMaterial/c tomcat.connector.https.keyMaterial=${tomcat_key_material}" ${install_dir}/conf/catalina.properties
    dos2unix ${install_dir}/conf/catalina.properties > /dev/null 2>&1
    dos2unix ${install_dir}/conf/server.xml > /dev/null 2>&1
    log_info "Update password successfully." 
    cd ${current_dir}
}

function log_file_deal()
{
    touch /var/log/plugin/install_${vmware_name}.log
    chown root:robogrp /var/log/plugin/install_${vmware_name}.log
    chmod 640 /var/log/plugin/install_${vmware_name}.log
    touch /var/log/plugin/replace_certs.log
    chown root:robogrp /var/log/plugin/replace_certs.log
    chmod 640 /var/log/plugin/replace_certs.log
    touch /var/log/plugin/${vmware_name}_pwd.log
    chown root:robogrp /var/log/plugin/${vmware_name}_pwd.log
    chmod 640 /var/log/plugin/${vmware_name}_pwd.log
    touch /var/log/plugin/${vmware_name}_certificate_check.log
    chown root:robogrp /var/log/plugin/${vmware_name}_certificate_check.log
    chmod 640 /var/log/plugin/${vmware_name}_certificate_check.log
    touch /var/log/plugin/${vmware_name}_vmware_user.log
    chown root:robogrp /var/log/plugin/${vmware_name}_vmware_user.log
    chmod 640 /var/log/plugin/${vmware_name}_vmware_user.log
}
function installTomcat()
{
    cd ${current_dir}
    cp -r ${install_temp_dir}/tomcat/* ${install_dir}/
    cp ${install_dir}/lib/commons-codec-*.jar ${main_dire}/sbin/
    cp ${install_dir}/lib/Http11Protocol.jar ${main_dire}/sbin/
    chmod 500 ${main_dire}/sbin/*
    chown root:root -R ${main_dire}/sbin
    ######修改配置
    cp ${current_dir}/conf/configTemplate/server.xml ${install_dir}/conf/server.xml
    cp ${current_dir}/conf/configTemplate/vmware_plugin.service /usr/lib/systemd/system/${vmware_name}.service
    sed -i "s/vmware_plugin/${vmware_name}/g" /usr/lib/systemd/system/${vmware_name}.service
    echo "tomcat.connector.https.keyMaterial=replace_object" >> ${install_dir}/conf/catalina.properties


    ######修改权限
    chown root:root -h ${main_dire}
    chown root:root -R -h ${main_dire}/conf
    chown vmware:plugin -R -h ${main_dire}/tomcat
    chmod 755  ${main_dire}
    chmod 755 /opt/plugin
    chmod 755 /opt/plugin/cert
    chmod -R 700 ${install_dir}
    chmod 600 ${install_dir}/bin/*
    chmod 700 ${install_dir}/bin/*.sh
    chmod 600 ${install_dir}/conf/*
    chmod 700 ${install_dir}/logs
    chmod 700 ${install_dir}/webapps
    log_file_deal

    sed -i "s/CATALINA_OUT=/#&/g" ${install_dir}/bin/catalina.sh
    sed -i "/CATALINA_OUT=/a\  CATALINA_OUT=/var/log/plugin/${vmware_name}/catalina.out" ${install_dir}/bin/catalina.sh
    sed -i "s#\${catalina.base}/logs#/var/log/plugin/${vmware_name}#g" ${install_dir}/conf/logging.properties
    sed -i "/^2localhost.org/ s/FINE/OFF/g" ${install_dir}/conf/logging.properties
    sed -i "/^3manager.org/ s/FINE/OFF/g" ${install_dir}/conf/logging.properties
    sed -i "/^4host-manager.org/ s/FINE/OFF/g" ${install_dir}/conf/logging.properties
    mkdir -p /var/log/plugin/${vmware_name}
    chmod 755 /var/log/plugin
    chmod 750 /var/log/plugin/${vmware_name}
    chown vmware:robogrp /var/log/plugin/${vmware_name}
    chmod 500 ${main_dire}/sbin/*
    chown root:root -R ${main_dire}/sbin
    change_keystore_password
}
function deletetemdir()
{
    rm -rf ${install_temp_dir}
}

function add_file()
{
    if [[ ! -d /opt/plugin/sbin ]];then
        mkdir -p /opt/plugin/sbin
    fi
    if [[ ! -f /opt/plugin/sbin/replace_certs.sh ]];then
        cp ${current_dir}/replace_certs.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/replace_certs.sh
    fi

    cp ${current_dir}/vmware_tool.py ${tool_file}
    cp ${install_cur_dir}/uninstall.sh ${main_dire}/sbin
    cp ${current_dir}/vmware_pass_update.py ${tool_file}
    cp ${current_dir}/replace_vmware_plugin_certs.sh ${tool_file}
    cp ${current_dir}/file_check.sh ${tool_file}
    if [[ ! -f /opt/plugin/vmware_information_tool.sh ]];then
        cp ${current_dir}/vmware_information_tool.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/vmware_information_tool.sh
    fi
    if [[ ! -f /opt/plugin/vmware_info_tool.sh ]];then
        cp ${current_dir}/vmware_info_tool.sh /opt/plugin/
        chmod 550 /opt/plugin/vmware_info_tool.sh
        chown vmware:robogrp /opt/plugin/vmware_info_tool.sh
    fi

    if [[ ! -f /opt/plugin/replace_vmware_certs.sh ]];then
        cp ${current_dir}/replace_vmware_certs.sh /opt/plugin/
        chmod 550 /opt/plugin/replace_vmware_certs.sh
        chown vmware:robogrp /opt/plugin/replace_vmware_certs.sh
    fi

    if [[ ! -f /opt/plugin/sbin/uninstall.sh ]];then
        cp ${install_cur_dir}/uninstall.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/uninstall.sh
    fi

    cp ${current_dir}/vmware_tool.sh /opt/plugin/${vmware_name}/
    chmod 550 /opt/plugin/${vmware_name}/vmware_tool.sh
    chown vmware:robogrp /opt/plugin/${vmware_name}/vmware_tool.sh
    echo "sudo /opt/plugin/${vmware_name}/tool/vmware_tool.py \$@" >> /opt/plugin/${vmware_name}/vmware_tool.sh

    chmod 500 ${tool_file}/*
    chown root:root -R -h ${tool_file}
    cp ${current_dir}/cert_gen.sh ${main_dire}/sbin
    cp ${current_dir}/openssl.cnf ${main_dire}/sbin
    cp ${current_dir}/vmware_cert_check.sh ${main_dire}/sbin

    for item in `ls ${tool_file}`;do
        sed -i "s/vmware_plugin/${vmware_name}/g" ${tool_file}/${item}
        sed -i "s/19091/${vmware_getport}/g" ${tool_file}/${item}
    done
    for item in `ls ${main_dire}/sbin`;do
        sed -i "s/vmware_plugin/${vmware_name}/g" ${main_dire}/sbin/${item}
    done

    chmod 500 ${main_dire}/sbin/*
    chown root:root -R -h ${main_dire}/sbin

    cp ${install_cur_dir}/conf/install.conf ${conf_file}
    chown root:root ${conf_file}
    echo "" >> ${conf_file}/install.conf
    echo "vmware_name=${vmware_name}" >> ${conf_file}/install.conf
    echo "vmware_getport=${vmware_getport}" >> ${conf_file}/install.conf
    echo "vmware_outport=${vmware_outport}" >> ${conf_file}/install.conf
}

function add_sudoer(){
    grep 'vmware_information_tool.sh' /etc/sudoers.d/robomanager >> /dev/null 2>&1
    if [[ $? -ne 0 ]];then
        echo 'robomanager ALL=(root) NOPASSWD: /opt/plugin/sbin/vmware_information_tool.sh' >> /etc/sudoers.d/robomanager
        echo 'robomanager ALL=(root) NOPASSWD: /opt/plugin/sbin/replace_certs.sh' >> /etc/sudoers.d/robomanager
    fi
    echo "robomanager ALL=(root) NOPASSWD: /opt/plugin/${vmware_name}/tool/vmware_tool.py" >> /etc/sudoers.d/robomanager

}

function cps_check(){
    ps -ef |grep -v grep |grep /opt/${obj_name}/robo/cps-monitor/cps_monitor/cps_monitor.py >> /dev/null 2>&1
    fn_log "cps-monitor health check"
}

function add_cps() {
##加入cps-monitor
    ${reg_command_file} reg ${vmware_name} --type 1 --cmd "/opt/plugin/${vmware_name}" --proc-num 1 --heartbeat-timeout 20 --heartbeat-interval 15 --no-control  --restart-script "sudo /bin/systemctl restart ${vmware_name}"
    grep "${vmware_name}" /etc/sudoers.d/robouser >> /dev/bull 2>&1
    if [[ $? -ne 0 ]];then
        echo "robouser ALL=(root) NOPASSWD: /bin/systemctl restart ${vmware_name}" >> /etc/sudoers.d/robouser
        echo "" >> /etc/sudoers.d/robouser
    fi
}

function log_config()
{
if [[ ! -f ${logrotate_file} ]]; then
    touch ${logrotate_file}
    chown -h vmware:robogrp ${logrotate_file}
    chmod 600 ${logrotate_file}
fi
local crontable_file=/etc/cron.d/FusionCube
grep logrotate_vmware ${logrotate_file} >> /dev/bull 2>&1
if [[ $? -ne 0 ]]; then
    echo "*/10 * * * *  vmware /usr/sbin/logrotate ${logrotate_file} -s /home/vmware/logrotate.status && rm -f /home/vmware/logrotate.status" >> ${crontable_file}
fi
grep '/var/log/plugin/\*\.log' ${logrotate_file} >> /var/log/plugin/install_${vmware_name}.log 2>&1
if [[ $? -eq 0 ]]; then
    cat << EOF >> ${logrotate_file}
${logrotate_path}/*.log {
    missingok
    rotate 10
    compress
    size=100M
    copytruncate
    create 0640 vmware robogrp
}
${logrotate_path}/*.out {
    missingok
    rotate 10
    compress
    size=100M
    copytruncate
    create 0640 vmware robogrp

}
EOF
else
    cat << EOF >> ${logrotate_file}
${logrotate_path}/*.log {
    missingok
    rotate 10
    compress
    size=100M
    copytruncate
    create 0640 vmware robogrp
}
${logrotate_path}/*.out {
    missingok
    rotate 10
    compress
    size=100M
    copytruncate
    create 0640 vmware robogrp
}
/var/log/plugin/*.log {
    missingok
    rotate 10
    compress
    size=100M
    copytruncate
    create 0640 vmware robogrp
}
EOF
fi
}

function auto_start(){
    systemctl stop ${vmware_name}.service
    systemctl enable ${vmware_name}.service
}

cps_check
createDir
add_file
add_sudoer
gen_cert
add_timerOfCheckVmware_jks >> /dev/null 2>&1
extractTempateTomcat
installTomcat
deletetemdir
auto_start >> /var/log/plugin/install_${vmware_name}.log 2>&1
add_cps
log_config
