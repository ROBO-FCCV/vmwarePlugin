#!/usr/bin/python

#  Copyright (c). 2021-2021. All rights reserved.

import getpass
import json
import logger
import os
import re
import requests
import yaml
from requests.packages.urllib3.exceptions import InsecureRequestWarning
from sys import argv

from vmware_pass_update import UpdatePassword

requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

EXIT_ENTER_TO_CONTINUE = "Enter n to exit / enter to continue: "
V_CENTER_ID_FAIL = "Input vCenter ID fail!!"
IP_FORMAT_ERROR = "IP format error!"
INPUT_ERROR = "Input error"
LOG_PATH = "/var/log/plugin/vmware_plugin_vmware_user.log"
HOST = 'https://127.0.0.1:19091'
VMWARE_PATH = '/vmware/vmware'
VCENTER_SUM = 20


class VmwareUser:
    def __init__(self):
        self.username = None
        self.password = None
        self.header_token = {
            'Content-Type': 'application/json;charset=UTF-8'
        }
        self.logger = logger.init(LOG_PATH)
        self.vmware_yml = "/opt/plugin/vmware_plugin/" \
                          "tomcat/webapps/vmware/WEB-INF/classes/vmware.yml"

    @staticmethod
    def init_plugin(username, password):
        r"""
        Init plugin
        :param username: username
        :param password: password
        :return: init result
        """
        uri = HOST + '/vmware/init'
        body = {'username': username, 'password': password}
        response = requests.post(url=uri, json=body, verify=False)
        if response.status_code == 200:
            resp = json.loads(response.content)
            if resp.get("code") == 0:
                print("The plugin init success.")
                exit(0)
            else:
                print(resp.get("msg"))
                exit(1)
        else:
            print('Tomcat application error.Make sure the tomcat plugin '
                  'running.')
            exit(1)

    def login(self):
        r"""
        Login
        :return: login result
        """
        uri = HOST + '/vmware/login'
        body = {'username': self.username, 'password': self.password}
        response = requests.post(url=uri, json=body, headers=self.header_token,
                                 verify=False)
        if response.status_code == 200:
            resp = json.loads(response.content)
            if resp.get("code") != '0':
                print(resp.get("msg"))
                exit(1)
            else:
                self.header_token = {
                    'Content-Type': 'application/json;charset=UTF-8',
                    'Authorization': 'Bearer ' + resp.get("data")
                }
                return self.header_token
        else:
            print('Tomcat application error.Make sure the tomcat plugin '
                  'running.')
            exit(1)
            return None

    def list_vmware(self, head_token):
        r"""
        Check if the token was correct
        :param head_token: head params with token
        :return: head token
        """
        uri = HOST + '/vmware/vmware'
        response = requests.get(url=uri, headers=head_token, verify=False)
        if response.status_code == 200:
            return head_token
        else:
            return self.login()

    def get_token(self):
        r""" Get token with username and password.

        :return: head token
        """
        if self.header_token is not None:
            token = self.list_vmware(head_token=self.header_token)
        else:
            token = self.login()
        return token

    @staticmethod
    def check_ip(host_ip):
        r"""
        Check ip address
        :param host_ip: host ip
        :return: boolean
        """
        pat = re.compile(r'([0-9]{1,3})\.')
        r = re.findall(pat, host_ip + ".")
        if len(r) == 4 and len([x for x in r if
                                0 <= int(x) <= 255]) == 4:
            return True
        else:
            return False

    @staticmethod
    def confirm():
        r"""
        Confirm print
        :return: Boolean
        """
        notice = input(EXIT_ENTER_TO_CONTINUE)
        if not notice:
            return True
        elif notice == 'n':
            exit(0)
        else:
            print(INPUT_ERROR)
            exit(1)

    def user_identity(self):
        r"""
        Input username and password for this obj
        :return: none
        """
        self.username = input("Please enter user name(default admin): ")
        if len(self.username) == 0:
            self.username = 'admin'
        count_pass = 0
        while count_pass < 4:
            self.password = getpass.getpass(prompt="Please enter user "
                                                   "password: ")
            count_pass = count_pass + 1
            if len(self.password) == 0:
                print('Password is empty, please re-input.')
                continue
            return
        print("Input error, please check!")
        exit(1)

    def vmware_get_ip_update(self):
        r"""
        Vmware get ip for update
        :return: ip
        """
        count_ip = 0
        while count_ip < 4:
            vmware_ip = input("Please enter the old vCenter IP: ")
            if not self.check_ip(vmware_ip):
                print(IP_FORMAT_ERROR)
                count_ip += 1
            elif not self.vmware_find_by_ip(vmware_ip):
                print("The vCenter IP not exists!")
                count_ip += 1
            else:
                return vmware_ip
        print(V_CENTER_ID_FAIL)
        exit(1)

    def vmware_get_ip_confirm(self, action):
        r"""
        Vmware get ip for confirm
        :return: ip
        """
        count_ip = 0
        while count_ip < 4:
            if action == "update":
                vmware_ip = input("Please enter the "
                                  "vCenter IP(default old ip): ")
                if len(vmware_ip) == 0:
                    return True
            elif action == "add":
                vmware_ip = input("Please enter the vCenter IP: ")
                if len(vmware_ip) == 0:
                    print("Input error,Please retry!!")
                    count_ip += 1
                    continue
            if not self.check_ip(vmware_ip):
                print(IP_FORMAT_ERROR)
                count_ip += 1
            else:
                return vmware_ip
        print(V_CENTER_ID_FAIL)
        exit(1)

    def vmware_get_delete_ip(self):
        r"""
        Get ip for delete
        :return: delete ip address
        """
        count_ip = 0
        while count_ip < 4:
            vmware_ip = input("Please enter the vCenter IP: ")
            if not self.check_ip(vmware_ip):
                print("IP format error!")
                count_ip += 1
            elif not self.vmware_find_by_ip(vmware_ip):
                print("The vCenter IP not exists!")
                count_ip += 1
            else:
                return vmware_ip
        print("Input vCenter ID fail!!")
        exit(1)

    @staticmethod
    def vmware_get_username():
        r"""
        Get username
        :return: username
        """
        count_user = 0
        while count_user < 4:
            vmware_user = input("Please enter the vCenter user name: ")
            if not vmware_user:
                print("The input is null,please enter again!")
                count_user = count_user + 1
            else:
                return vmware_user
        print("Input vCenter USER fail!!")
        exit(1)

    @staticmethod
    def check_pass_repeat(pwd1, pwd2):
        r"""
        Check password equals
        :param pwd1: password
        :param pwd2: password repeat
        :return: boolean
        """
        if pwd1 == pwd2:
            return True
        else:
            return False

    def vmware_get_pass(self, repeat=True):
        r"""
        Vmware get password
        :param repeat: repeat
        :return: password
        """
        count_pass = 0
        while count_pass < 4:
            vmware_password = getpass.getpass("Please enter the "
                                              "vCenter password: ")
            if not vmware_password:
                print("The input is null,please enter again!")
                count_pass = count_pass + 1
                continue
            if repeat:
                vmware_password2 = getpass.getpass("Please enter the "
                                                   "vCenter password again: ")
                if not self.check_pass_repeat(vmware_password, vmware_password2):
                    print('The password you entered is different, '
                          'please re-enter !')
                    continue
            return vmware_password
        print("Input vCenter password fail!!")
        exit(1)

    def vmware_add_to_plugin(self):
        r"""
        Add vmware to plugin
        :return: boolean
        """
        vmware_ip = self.vmware_get_ip_confirm("add")
        if self.vmware_find_by_ip(vmware_ip):
            print("The vCenter IP already exists!")
            exit(1)
        vmware_obj = {'ip': vmware_ip, 'username': self.vmware_get_username()}
        password = self.vmware_get_pass(False)
        url = HOST + VMWARE_PATH
        body = {
            "ip": vmware_obj['ip'],
            "username": vmware_obj['username'],
            "password": password,
        }
        try:
            response = requests.post(url, json=body,
                                     headers=self.header_token,
                                     verify=False, timeout=100)
            return self.check_plugin_result(response)
        except Exception as e_info:
            self.logger.error(e_info)
            return False

    def vmware_update_to_plugin(self):
        r"""
        Update vmware to plugin
        :return: boolean
        """
        old_ip = self.vmware_get_ip_update()
        confirm_ip = self.vmware_get_ip_confirm("update")
        vmware = self.vmware_find_by_ip(old_ip)
        if confirm_ip is True:
            ip = old_ip
        else:
            ip = confirm_ip
        username = self.vmware_get_username()
        password = self.vmware_get_pass(repeat=False)
        url = HOST + VMWARE_PATH + "/" + vmware.get('id')
        body = {
            "ip": ip,
            "username": username,
            "password": password,
        }
        try:
            response = requests.put(url, json=body,
                                    headers=self.header_token,
                                    verify=False, timeout=100)
            return self.check_plugin_result(response)
        except Exception as e_info:
            self.logger.error(e_info)
            return False

    def vmware_delete_to_plugin(self, delete_ip):
        r"""
        Delete vmware to plugin
        :param delete_ip: ip address
        :return: boolean
        """
        vmware = self.vmware_find_by_ip(delete_ip)
        vmware_id = vmware.get('id')
        url = HOST + VMWARE_PATH + "/" + vmware_id
        try:
            response = requests.delete(url, headers=self.header_token, verify=False)
            return self.check_plugin_result(response)
        except Exception as e_info:
            self.logger.error(e_info)
            return False

    def check_plugin_result(self, response):
        r"""
        Check plugin result
        :param response: http response
        :return: result
        """
        get_data = json.loads(response.content).get("code")
        if get_data == '0':
            return True
        else:
            self.logger.info(json.loads(response.content).get("msg"))
            print(json.loads(response.content).get("msg"))
            return False

    def add_vmware_info(self):
        r"""
        Add vmware info
        :return: None
        """
        self.user_identity()
        self.logger.info('Add vmware information begin.')
        self.get_token()
        while True:
            notice = self.confirm()
            vmware_count = self.vmware_count()
            if vmware_count >= VCENTER_SUM:
                print("The number of vcenter account has reached the limit.")
                exit(1)
            if notice:
                contents = self.vmware_add_to_plugin()
                if not contents:
                    print('Add vmware information failed!')
                    continue
                print('Add %s success.' % notice)
                self.logger.info('Add %s success.' % notice)

    def delete_vmware_info(self):
        r"""
        Delete vmware information
        :return: None
        """
        self.user_identity()
        self.logger.info('Delete vmware information begin.')
        self.get_token()
        while True:
            notice = self.confirm()
            if notice:
                self.delete_vmware_action()

    def delete_vmware_action(self):
        r"""
        Delete vmware
        :return: None
        """
        delete_ip = self.vmware_get_delete_ip()
        confirm_notice = input("Please enter [y/n] to confirm: ")
        if confirm_notice == 'n':
            exit(0)
        elif confirm_notice == 'y':
            if self.vmware_delete_to_plugin(delete_ip):
                print(' Delete %s success.' % delete_ip)
                self.logger.info('Delete %s success.' % delete_ip)
            else:
                print(' Delete %s fail.' % delete_ip)
                self.logger.error('Delete %s fail.' % delete_ip)
        else:
            print(INPUT_ERROR)
            exit(1)

    def update_vmware_info(self):
        r"""
        Update vmware
        :return: None
        """
        self.user_identity()
        self.logger.info('Update vmware information begin.')
        self.get_token()
        while True:
            notice = self.confirm()
            if notice:
                contents = self.vmware_update_to_plugin()
                if not contents:
                    print('Update vmware information fail!')
                else:
                    print('Update vmware success.')
                    self.logger.info('Update vmware success.')

    def show_vmware_info(self):
        r"""
        Show vmware information
        :return: None
        """
        self.check_link()
        with open(self.vmware_yml, 'r') as load_file:
            load_dict = yaml.safe_load(load_file)
            if load_dict is None:
                print("---------------------------------------")
                print("No vmware information.")
                exit(0)
            vmware_items = load_dict.get('vmware').get('configs')
            if vmware_items:
                for (k, v) in vmware_items.items():
                    print("---------------------------------------")
                    print("id : {}".format(v.get('id')))
                    print("ip : {}".format(v.get('ip')))
                    print("username : {}".format(v.get('username')))
            else:
                print("No vmware information.")
                exit(0)

    def init_vmware_info(self):
        self.username = input("Please enter init plugin username(default "
                              "admin): ")
        if len(self.username) == 0:
            self.username = 'admin'
        password_check = UpdatePassword()
        password = password_check.vmware_get_pass()
        self.init_plugin(username=self.username, password=password)

    @staticmethod
    def usage():
        r"""
        Print command
        :return: None
        """
        print('''usage: python vmware_tool.py {add|delete|update|display|init}''')

    @staticmethod
    def find_item(obj, ip):
        r"""
        Find item
        :param obj: obj array
        :param ip: ip address
        :return: item
        """
        for (k, v) in obj.items():
            if v.get('ip') == ip:
                return v
        return None

    def vmware_find_by_ip(self, ip):
        r"""
        Find vmware obj by ip
        :param ip: ip address
        :return: vmware obj
        """
        self.check_link()
        with open(self.vmware_yml, 'r') as load_file:
            load_dict = yaml.safe_load(load_file)
            vmware_items = load_dict.get('vmware').get('configs')
            if vmware_items:
                return self.find_item(vmware_items, ip)
            else:
                return None

    def vmware_count(self):
        r"""
        Query access vmware count
        :return: access vmware count
        """
        self.check_link()
        with open(self.vmware_yml, 'r') as load_file:
            load_dict = yaml.safe_load(load_file)
            if load_dict is None:
                return 0
            if load_dict.get('vmware').get('configs'):
                return len(load_dict.get('vmware').get('configs'))
            else:
                return 0

    def check_link(self):
        if os.path.islink(self.vmware_yml):
            print(self.vmware_yml + "contains link")
            exit(1)


if __name__ == '__main__':
    manager = VmwareUser()
    if len(argv) != 2:
        manager.usage()
        exit(1)
    try:
        if argv[1] == 'add':
            manager.add_vmware_info()
        elif argv[1] == "delete":
            manager.delete_vmware_info()
        elif argv[1] == "update":
            manager.update_vmware_info()
        elif argv[1] == "display":
            manager.show_vmware_info()
        elif argv[1] == "init":
            manager.init_vmware_info()
        else:
            manager.usage()
    except KeyboardInterrupt:
        print('Terminated.')
    exit(0)
