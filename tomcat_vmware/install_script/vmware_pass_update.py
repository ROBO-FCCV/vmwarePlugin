#!/usr/bin/python

#  Copyright (c). 2021-2021. All rights reserved.

import getpass
import json
import re
import subprocess
import sys
from sys import argv

import logging
import requests
from requests.packages.urllib3.exceptions import InsecureRequestWarning

try:
    sys.path.append('/opt/object_name/robo/sbin')
    import check_pwd
except Exception as e:
    raise e

requests.packages.urllib3.disable_warnings(InsecureRequestWarning)
HOST = 'https://127.0.0.1:19091'
URL_SUB = '/vmware/update-pwd'
# auth
LOG_PATH = "/var/log/plugin/vmware_plugin_pwd.log"


def init_log(logfile):
    log = logging.getLogger('user_pwd')
    logging.basicConfig(level=logging.INFO,
                        format=('%(asctime)s [%(levelname)s] '
                                '%(filename)s :%(lineno)d %(message)s'),
                        datefmt='%Y-%m-%d %H:%M:%S',
                        filename=logfile)
    return log


class UpdatePassword:
    def __init__(self):
        self.user = None
        self.password = None
        self.header_token = {
            'Content-Type': 'application/json;charset=UTF-8'
        }
        self.logger = init_log(LOG_PATH)
        self.new_password = None

    def user_identity(self):
        uri = 'https://127.0.0.1:19091/vmware/login'
        self.user = input("Please enter user name(default admin): ")
        if len(self.user) == 0:
            self.user = 'admin'
        while True:
            self.password = getpass.getpass("Please enter user password: ")
            if len(self.password) == 0:
                print('Password is empty, please re-input.')
                continue
            break
        body = {'username': self.user, 'password': self.password}
        response = requests.post(
            url=uri, json=body,
            headers=self.header_token, verify=False)
        if response.status_code == 200:
            resp = json.loads(response.content)
            if resp.get('code') != '0':
                print(resp.get('msg'))
                exit(1)
            else:
                self.header_token = {
                    'Content-Type': 'application/json;charset=UTF-8',
                    'Authorization': 'Bearer ' + resp.get("data")
                }
                return True
        else:
            print("connect to server error.")
            exit(1)
            self.logger.info("login failed, error:%s" % response.content)

    def run_cmd(self, cmd):
        try:
            (status, output) = subprocess.getstatusoutput(cmd)
            if status == 0:
                return output
            else:
                self.logger.info(output)
                print(output)
        except Exception as e_info:
            self.logger.error(e_info)
            return False

    @staticmethod
    def check_pass_contain_upper(password):
        pattern = re.compile('[A-Z]+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    @staticmethod
    def check_pass_contain_lower(password):
        pattern = re.compile('[a-z]+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    @staticmethod
    def check_pass_contain_num(password):
        pattern = re.compile('[0-9]+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    @staticmethod
    def check_pass_contain_symbol(password):
        pattern = re.compile('([#$%&()*+_,./:;<=>?@^{|}~])+')
        match = pattern.findall(password)
        if match:
            return True
        else:
            return False

    @staticmethod
    def check_pwd_data(password):
        body = {
            "password": password
        }
        result = check_pwd.check_data(body)
        if result == 0:
            return True
        else:
            return False

    @staticmethod
    def check_pass_repeat(pwd1, pwd2):
        if pwd1 == pwd2:
            return True
        else:
            return False

    def vmware_get_pass(self):
        count_pass = 0
        while count_pass < 4:
            vmware_password = getpass.getpass("Please enter the new "
                                              "password: ")
            count_pass = count_pass + 1
            if len(vmware_password) < 8:
                print('The length of new password is less than 8.')
                continue
            if self.check_pass_contain_upper(vmware_password) is False:
                print('The new password must contain at least one '
                      'uppercase letter.')
                continue
            if self.check_pass_contain_lower(vmware_password) is False:
                print('The new password must contain at least one '
                      'lowercase letter.')
                continue
            if self.check_pass_contain_num(vmware_password) is False:
                print('The new password must contain at least one digit.')
                continue
            if self.check_pass_contain_symbol(vmware_password) is False:
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
            if not self.check_pass_repeat(vmware_password, vmware_password2):
                print('The password you entered is different, '
                      'please re-enter !')
                continue
            return vmware_password2

    def update_vmware_new_password(self):
        url = HOST + URL_SUB
        try:
            self.new_password = self.vmware_get_pass()
            body = {
                "newPassword": self.new_password,
                "username": self.user,
                "password": self.password
            }
            response = requests.put(url, json=body,
                                    headers=self.header_token,
                                    verify=False, timeout=10)
            if response.status_code == 200:
                resp = json.loads(response.content)
                if resp.get('code') != '0':
                    print(resp.get('msg'))
                    exit(1)
                else:
                    print("Update vmware password success.")
                    self.header_token = {
                        'Content-Type': 'application/json;charset=UTF-8',
                        'Authorization': 'Bearer ' + resp.get("data")
                    }
                    self.run_cmd(
                        'systemctl restart vmware_plugin.service >> '
                        '/dev/null 2>&1')
                    self.logger.info('Update vmware password success.')
            else:
                print("Connect to server error.")
                exit(1)
                self.logger.info(
                    "Update password failed, error:%s" % response.content)
        except Exception as e_info:
            print(e_info)
            exit(1)

    @staticmethod
    def usage():
        print('''Usage:  python vmware_pass_update.py ''')

    def update_pass(self):
        if not self.user_identity():
            exit(1)
        self.logger.info('Update vmware password begin.')
        self.update_vmware_new_password()


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
