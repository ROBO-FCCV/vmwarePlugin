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
logrotate_file=/etc/logrotate.d/logrotate_all
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
        echo "*/10 * * * * root find /var/log/plugin -name \"*.gz\"  -print0  | xargs -0 chmod 400 " >> ${crontab_file}
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
    echo "    <security-constraint>
        <web-resource-collection>
            <url-pattern>/*</url-pattern>
            <http-method>OPTIONS</http-method>
            <http-method>HEAD</http-method>
            <!--<http-method>PUT</http-method>
            <http-method>DELETE</http-method>-->
            <http-method>TRACE</http-method>
        </web-resource-collection>
        <auth-constraint>
        </auth-constraint>
    </security-constraint>

    <error-page>
        <error-code>400</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>401</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>402</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>403</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>404</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>405</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>406</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>407</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>413</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>414</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>500</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <error-code>501</error-code>
        <location>/error.html</location>
    </error-page>
    <error-page>
        <exception-type>java.lang.Throwable</exception-type>
        <location>/error.html</location>
    </error-page>

</web-app>
" >> ${install_temp_dir}/tomcat/conf/web.xml
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
    local de_pass=`python -c "import kmc.kmc;print(kmc.kmc.API().decrypt(0, '${en_pass}'))"`
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
    sed -i "/keystorePass=/c keystorePass=\"${tomcat_key}\"" ${install_dir}/conf/server.xml
    sed -i s#maxPostSize\=\"53687091200\"#maxPostSize\=\"1024\"#g ${install_dir}/conf/server.xml
    sed -i "/tomcat.connector.https.keyMaterial/c tomcat.connector.https.keyMaterial=${tomcat_key_material}" ${install_dir}/conf/catalina.properties
    dos2unix ${install_dir}/conf/catalina.properties > /dev/null 2>&1
    dos2unix ${install_dir}/conf/server.xml > /dev/null 2>&1
    log_info "Update password successfully." 
    cd ${current_dir}
}

function installTomcat()
{
    cd ${current_dir}
    cp -r ${install_temp_dir}/tomcat/* $install_dir/

    ######修改配置
    cp ${current_dir}/conf/configTemplate/server.xml $install_dir/conf/server.xml
    cp ${current_dir}/conf/configTemplate/vmware_plugin.service /usr/lib/systemd/system/${vmware_name}.service
    sed -i "s/vmware_plugin/${vmware_name}/g" /usr/lib/systemd/system/${vmware_name}.service
    echo "tomcat.connector.https.keyMaterial=replace_object" >> ${install_dir}/conf/catalina.properties


    ######修改权限
    chown -R vmware:plugin $main_dire
    chmod 755 /opt/plugin
    chmod 755 /opt/plugin/cert
    chmod -R 700 ${install_dir}
    chmod 600 ${install_dir}/bin/*
    chmod 700 ${install_dir}/bin/*.sh
    chmod 600 ${install_dir}/conf/*
    chmod 700 ${install_dir}/logs
    chmod 700 ${install_dir}/webapps

    sed -i "s/CATALINA_OUT=/#&/g" ${install_dir}/bin/catalina.sh
    sed -i "/CATALINA_OUT=/a\  CATALINA_OUT=/var/log/plugin/${vmware_name}/catalina.out" ${install_dir}/bin/catalina.sh
    sed -i "s#\${catalina.base}/logs#/var/log/plugin/${vmware_name}#g" ${install_dir}/conf/logging.properties
    sed -i "/^2localhost.org/ s/FINE/OFF/g" ${install_dir}/conf/logging.properties
    sed -i "/^3manager.org/ s/FINE/OFF/g" ${install_dir}/conf/logging.properties
    sed -i "/^4host-manager.org/ s/FINE/OFF/g" ${install_dir}/conf/logging.properties
    mkdir -p /var/log/plugin/${vmware_name}
    chmod 755 /var/log/plugin
    chmod 750 /var/log/plugin/${vmware_name}
    chown vmware:plugin /var/log/plugin/${vmware_name}
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
    if [[ ! -f /opt/plugin/sbin/registry_cps.sh ]];then
        cp ${current_dir}/registry_cps.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/registry_cps.sh
    fi
    if [[ ! -f /opt/plugin/sbin/replace_certs.sh ]];then
        cp ${current_dir}/replace_certs.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/replace_certs.sh
    fi

    cp ${current_dir}/vmware_tool.py ${tool_file}
    cp ${install_cur_dir}/uninstall.sh ${main_dire}/sbin
    cp ${current_dir}/vmware_pass_update.py ${tool_file}
    cp ${current_dir}/replace_vmware_plugin_certs.sh ${tool_file}
    if [[ ! -f /opt/plugin/vmware_information_tool.sh ]];then
        cp ${current_dir}/vmware_information_tool.sh /opt/plugin
        chmod 500 /opt/plugin/vmware_information_tool.sh
    fi
    if [[ ! -f /opt/plugin/sbin/uninstall.sh ]];then
        cp ${install_cur_dir}/uninstall.sh /opt/plugin/sbin
        chmod 500 /opt/plugin/sbin/uninstall.sh
    fi
    chmod 500 ${tool_file}/*
    chown root:root -R ${tool_file}
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
    chown root:root -R ${main_dire}/sbin

    cp ${install_cur_dir}/conf/install.conf $conf_file
    echo "" >> ${conf_file}/install.conf
    echo "vmware_name=${vmware_name}" >> ${conf_file}/install.conf
    echo "vmware_getport=${vmware_getport}" >> ${conf_file}/install.conf
    echo "vmware_outport=${vmware_outport}" >> ${conf_file}/install.conf
}

function cps_check(){
    ps -ef |grep -v grep |grep /opt/${obj_name}/robo/cps-monitor/cps_monitor/cps_monitor.py >> /dev/null 2>&1
    fn_log "cps-monitor health check"
}

function add_cps() {
##加入cps-monitor
    ${reg_command_file} reg ${vmware_name} --type 1 --cmd "/opt/plugin/${vmware_name}" --proc-num 1 --heartbeat-timeout 20 --heartbeat-interval 15 --no-control  --restart-script "sudo /bin/systemctl restart ${vmware_name}"
    echo "robouser ALL=(root) NOPASSWD: /bin/systemctl restart ${vmware_name}" >> /etc/sudoers.d/robouser
    echo "" >> /etc/sudoers.d/robouser
}

function log_config(){
    cat << EOF >> ${logrotate_file}
${logrotate_path}/* {
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


