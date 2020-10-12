#!/usr/bin/python
import logger
import json
import commands
import requests
from sys import argv
from requests.packages.urllib3.exceptions import InsecureRequestWarning
import getpass
import re
import sys
try:
    sys.path.append('/opt/object_name/robo/sbin')
    import check_pwd
except Exception as e:
    raise e


requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

HOST = 'https://127.0.0.1:19091'
URL_SUB = '/vmware/v1/validateVmwareInfo'
PWD_ENCODE = '/vmware/encode'
# auth
LOG_PATH = "/var/log/plugin/vmware_plugin/pwd.log"


class UpdatePassword:
    def __init__(self):
        self.user = None
        self.authpass = None
        self.headers_token = None
        self.logger = logger.init(LOG_PATH)
        self.pass_path = "/opt/plugin/vmware_plugin/" \
                         "tomcat/webapps/vmware/WEB-INF/classes/login.yml"
        self.newpass = None

    def post(self, url, headers=None):
        try:
            response = requests.post(url, headers=headers, verify=False)
        except Exception:
            return None
        if response.status_code != 200:
            return None
        resp = json.loads(response.content)
        if resp['data']:
            return resp['data']
        return None

    def user_identity(self):
        uri = 'https://127.0.0.1:19091/vmware/login'
        self.user = raw_input("Please enter user name(default admin): ")
        if len(self.user) == 0:
            self.user = 'admin'
        while True:
            self.authpass = getpass.getpass("Please enter user password: ")
            if len(self.authpass) == 0:
                print('Password is empty, please re-input.')
                continue
            break
        headers = {'username': self.user, 'password': self.authpass}
        token = self.post(uri, headers)
        if not token:
            print('User or Password incorrect , get token failed.')
            exit(1)
        else:
            if token:
                self.headers_token = {
                'Content-Type': 'application/json;charset=UTF-8',
                    'X-Auth-Token': token
                }
                return True
            print("connect to server error.")
            self.logger.info("login failed, error:%s" % token)
            exit(1)

    def run_cmd(self, cmd):
        try:
            (status, output) = commands.getstatusoutput(cmd)
            if status == 0:
                return output
            else:
                self.logger.info(output)
                print(output)
        except Exception as e_info:
            self.logger.error(e_info)
            return False

    def checkpass_contain_upper(self, password):
        pattern = re.compile('[A-Z]+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    def checkpass_contain_lower(self, password):
        pattern = re.compile('[a-z]+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    def checkpass_contain_num(self, password):
        pattern = re.compile('[0-9]+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    def checkpass_contain_symbol(self, password):
        pattern = re.compile('([^a-z0-9A-Z])+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    def check_pwd_data(self, password):
        body = {
            "password": password
        }
        result = check_pwd.check_data(body)
        if result == 0:
            return True
        else:
            return False

    def checkpass_repeat(self, pwd1, pwd2):
        if pwd1 == pwd2:
            return True
        else:
            return False

    def vmware_get_pass(self):
        count_pass = 0
        while count_pass < 4:
            vmware_password = getpass.getpass("Please enter the new "
                                              "password: ")
            if len(vmware_password) < 8:
                print('The length of new password is less than 8.')
                continue
            if self.checkpass_contain_upper(vmware_password) is False:
                print('The new password must contain at least one '
                      'uppercase letter.')
                continue
            if self.checkpass_contain_lower(vmware_password) is False:
                print('The new password must contain at least one '
                      'lowercase letter.')
                continue
            if self.checkpass_contain_num(vmware_password) is False:
                print('The new password must contain at least one digit.')
                continue
            if self.checkpass_contain_symbol(vmware_password) is False:
                print('The new password must contain at least one of '
                      'the special characters inside the brackets'
                      '(!\'#$%&\'()*+_,./:;<=>?@[]^{_|}~).'
                      'Please re-enter a new password.')
                continue
            if self.check_pwd_data(vmware_password) is False:
                print('Password too weak.')
                continue
            vmware_password2 = getpass.getpass("Please enter the "
                                               "new password again: ")
            if not self.checkpass_repeat(vmware_password, vmware_password2):
                print('The password you entered is different, '
                      'please re-enter !')
                continue
            return vmware_password2
            break

    def update_vmware_newpass(self):
        url = HOST + PWD_ENCODE
        try:
            self.newpass = self.vmware_get_pass()
            body = {
                "password": self.newpass
            }
            response = requests.post(url, json=body,
                                     headers=self.headers_token,
                                     verify=False, timeout=10)
            get_data = json.loads(response.content)
            if int(get_data.get("code")) == 0:
                script1 = 'sed -i 4"c \  %s" %s' % \
                          (get_data.get("data"), self.pass_path)
                self.run_cmd(script1)
            else:
                print(get_data.get("msg"))
                self.logger.error(get_data.get("msg"))
            self.run_cmd('systemctl restart vmware_plugin.service')
            print("Update vmware password success.")
            self.logger.info('Update vmware password success.')

        except Exception as e_info:
            print(e_info)
            exit(1)

    def usage(self):
        print('''Usage:  python vmware_pass_update.py ''')

    def update_pass(self):
        if not self.user_identity():
            exit(1)
        self.logger.info('Update vmware password begin.')
        self.update_vmware_newpass()

if __name__ == '__main__':
    manager = UpdatePassword()
    if len(argv) != 1:
        manager.usage()
        exit(1)
    try:
        manager.update_pass()
    except KeyboardInterrupt:
        print('\nTerminated.')
        exit(1)
    exit(0)

