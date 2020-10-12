#!/usr/bin/python
import logger
import json
import commands
import requests
import kmc.kmc as k
from sys import argv
from requests.packages.urllib3.exceptions import InsecureRequestWarning
import getpass
import re
import os
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

LOG_PATH = "/var/log/plugin/vmware_plugin/vmware_user.log"
HOST = 'https://127.0.0.1:19091'
URL_SUB = '/vmware/v1/validateVmwareInfo'
VCENTER_SUM = 20


class VmwareUser:
    def __init__(self):
        self.user = None
        self.authpass = None
        self.headers_token = None
        self.logger = logger.init(LOG_PATH)
        self.file_path = "/opt/plugin/vmware_plugin/" \
                         "tomcat/webapps/vmware/WEB-INF/classes/vmware.yml"
        self.pass_path = "/opt/plugin/vmware_plugin/" \
                         "tomcat/webapps/vmware/WEB-INF/classes/login.yml"

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

    def run_cmd(self, cmd):
        try:
            (status, output) = commands.getstatusoutput(cmd)
            if status == 0:
                return output
            else:
                self.logger.info(output)
                return False
        except Exception as e_info:
            self.logger.error(e_info)
            return False

    def checkpass_repeat(self, pwd1, pwd2):
        if pwd1 == pwd2:
            return True
        else:
            return False

    def write_conf(self, content):
        with open(self.file_path, "a") as file_handle:
            file_handle.write(content)

    def vmware_count(self, name):
        with open(self.file_path, "r") as file_handle:
            content = file_handle.read()
            count = content.count(name)
            return count

    def checkip(self, hostip):
        pat = re.compile(r'([0-9]{1,3})\.')
        r = re.findall(pat, hostip + ".")
        if len(r) == 4 and len([x for x in r if
                                int(x) >= 0 and int(x) <= 255]) == 4:
            return True
        else:
            return False

    def confire(self):
        confire = raw_input("Please enter [y/n] to confirm: ")
        if confire == 'n':
            exit(0)
        elif confire == 'y':
            return True
        else:
            print("Input error")
            return False

    def user_identity(self):
        uri = HOST + '/vmware/login'
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
            return False
        else:
            if token:
                self.headers_token = {
                    'Content-Type': 'application/json;charset=UTF-8',
                    'X-Auth-Token': token
                }
                return True
            print("Connect to server error.")
            self.logger.info("login failed, error:%s" % token)
            return False

    def vmware_get_ip(self, action='update'):
        count_ip = 0
        if action == 'update':
            while count_ip < 4:
                vmware_ip = raw_input("Please enter the old vCenter IP: ")
                if not self.checkip(vmware_ip):
                    print("IP format error!")
                    count_ip = count_ip + 1
                    continue
                elif self.vmware_count(vmware_ip) == 0:
                    print("The vCenter IP not exists!")
                    count_ip = count_ip + 1
                    continue
                else:
                    return vmware_ip
                break
            print("Input vCenter ID fail!!")
            exit(1)
        elif action == 'confirm':
            while count_ip < 4:
                vmware_ip = raw_input("Please enter the "
                                      "vCenter IP(default old ip): ")
                if len(vmware_ip) == 0:
                    return True
                if not self.checkip(vmware_ip):
                    print("IP format error!")
                    count_ip = count_ip + 1
                    continue
                elif self.vmware_count(vmware_ip) > 0:
                    print("The vCenter IP already exists!")
                    count_ip = count_ip + 1
                    continue
                else:
                    return vmware_ip
                break
            print("Input vCenter ID fail!!")
            exit(1)
        elif action == 'add':
            while count_ip < 4:
                vmware_ip = raw_input("Please enter the vCenter IP: ")
                if not self.checkip(vmware_ip):
                    print('IP format error!')
                    count_ip = count_ip + 1
                    continue
                elif self.vmware_count(vmware_ip) > 0:
                    print("The vCenter IP already exists!")
                    count_ip = count_ip + 1
                    continue
                else:
                    return vmware_ip
                break
            print("Input vCenter ID fail!!")
            exit(1)

    def vmware_get_delete_ip(self):
        count_ip = 0
        while count_ip < 4:
            vmware_ip = raw_input("Please enter the vCenter IP: ")
            if not self.checkip(vmware_ip):
                print("IP format error!")
                count_ip = count_ip + 1
                continue
            elif self.vmware_count(vmware_ip) == 0:
                print("The vCenter IP not exists!")
                count_ip = count_ip + 1
                continue
            else:
                return vmware_ip
            break
        print("Input vCenter ID fail!!")
        exit(1)

    def vmware_get_username(self):
        count_user = 0
        while count_user < 4:
            vmware_user = raw_input("Please enter the vCenter user name: ")
            if not vmware_user:
                print("The input is null,please enter again!")
                count_user = count_user + 1
                continue
            else:
                return vmware_user
            break
        print("Input vCenter USER fail!!")
        exit(1)

    def vmware_get_pass(self, repeat=True):
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
                if not self.checkpass_repeat(vmware_password,
                                             vmware_password2):
                    print('The password you entered is different, '
                          'please re-enter !')
                    continue
            return vmware_password
            break
        print("Input vCenter password fail!!")
        exit(1)

    def vmware_title(self):
        vmware_num = 1
        while True:
            vmware_title = "Vmware%s:" % vmware_num
            if self.vmware_count(vmware_title) != 0:
                vmware_num += 1
                continue
            return vmware_title
            break

    def vmware_add_infor(self):
        infor = {}
        infor['ip'] = self.vmware_get_ip("add")
        infor['username'] = self.vmware_get_username()
        password = self.vmware_get_pass(False)
        infor['password'] = k.API().encrypt(0, password)
        url = HOST + URL_SUB
        body = {
            "ip": infor['ip'],
            "username": infor['username'],
            "password": password,
        }
        try:
            response = requests.post(url, json=body,
                                     headers=self.headers_token,
                                     verify=False, timeout=10)
            get_data = json.loads(response.content).get("data")
            if get_data.get("validationResult"):
                infor['vmwareId'] = get_data.get("vmwareId")
            else:
                self.logger.info(json.loads(response.content).get("msg"))
                print(json.loads(response.content).get("msg"))
                return False
        except Exception as e_info:
            self.logger.error(e_info)
            print('Add vmware user fail!')
            return False
        return infor

    def vmware_update_infor(self):
        infor = {}
        infor['old_ip'] = self.vmware_get_ip("update")
        confirm_ip = self.vmware_get_ip("confirm")
        if confirm_ip is True:
            infor['ip'] = infor.get('old_ip')
        else:
            infor['ip'] = confirm_ip
        infor['username'] = self.vmware_get_username()
        password = self.vmware_get_pass()
        infor['password'] = k.API().encrypt(0, password)
        url = HOST + URL_SUB
        body = {
            "ip": infor['ip'],
            "username": infor['username'],
            "password": password,
        }
        try:
            response = requests.post(url, json=body,
                                     headers=self.headers_token,
                                     verify=False, timeout=10)
            get_data = json.loads(response.content).get("data")
            if not get_data.get("validationResult"):
                self.logger.info(json.loads(response.content).get("msg"))
                return False
        except Exception as e_info:
            self.logger.error(e_info)
            return False

        script1 = 'sed -n "/%s/,/vmwareId/=" %s |tail -n1' % \
                  (infor['old_ip'], self.file_path)
        id_num = self.run_cmd(script1)
        script2 = "awk 'NR==%s {print}' %s | awk '{print $2}'" % \
                  (id_num, self.file_path)
        infor['vmwareId'] = self.run_cmd(script2)
        return infor

    def vmware_delete_infor(self, delte_ip):
        script1 = 'sed -n "/%s/,/vmwareId/=" %s |tail -n1' % \
                  (delte_ip, self.file_path)
        num = int(self.run_cmd(script1)) - 4
        script2 = 'sed -i "%s,%sd" %s' % \
                  (str(num), self.run_cmd(script1), self.file_path)
        result = os.system(script2)
        if result == 0:
            return True
        else:
            return False

    def add_vmware_info(self):
        if not self.user_identity():
            exit(1)
        self.logger.info('Add wmware user begin.')
        while True:
            notice = raw_input("Enter n to exit / enter to continue: ")
            if self.vmware_count("username") > VCENTER_SUM - 1:
                print("The number of vcenter accounts has reached the limit.")
                exit(0)
            if not notice:
                pass
            elif notice == 'n':
                exit(0)
            else:
                print("Input error")
                exit(1)
            contents = self.vmware_add_infor()
            if not contents:
                print('Add vmware user fail!')
                continue
            vmware_title = self.vmware_title()
            self.write_conf(vmware_title + '\n')
            for key, value in contents.items():
                content = "  " + key + ':' + " " + value + '\n'
                self.write_conf(content)
            print('Add %s success.' % contents['ip'])
            self.logger.info('Add %s success.' % contents['ip'])
            continue

    def delete_vmware_info(self):
        while True:
            notice = raw_input("Enter n to exit / enter to continue: ")
            if not notice:
                pass
            elif notice == 'n':
                exit(0)
            else:
                print("Input error")
                exit(1)
            delte_ip = self.vmware_get_delete_ip()
            confirm_notice = raw_input("Please enter [y/n] to confirm: ")
            if confirm_notice == 'n':
                exit(0)
            elif confirm_notice == 'y':
                pass
            else:
                print("Input error")
                exit(1)
            if self.vmware_delete_infor(delte_ip):
                print(' Delete %s success.' % delte_ip)
                self.logger.info('Delete %s success.' % delte_ip)
            else:
                print(' Delete %s fail.' % delte_ip)
                self.logger.error('Delete %s fail.' % delte_ip)

            continue

    def update_vmware_info(self):
        if not self.user_identity():
            exit(1)
        self.logger.info('Update wmware user begin.')
        while True:
            notice = raw_input("Enter n to exit / enter to continue: ")
            if not notice:
                pass
            elif notice == 'n':
                exit(0)
            else:
                print("Input error")
                exit(1)
            contents = self.vmware_update_infor()
            if not contents:
                print('Update vmware user fail!')
                continue
            self.vmware_delete_infor(contents['old_ip'])
            vmware_title = self.vmware_title()
            self.write_conf(vmware_title + '\n')
            for key, value in contents.items():
                if key == 'old_ip':
                    continue
                content = "  " + key + ':' + " " + value + '\n'
                self.write_conf(content)
            print('Update %s success.' % contents['ip'])
            self.logger.info('Update %s success.' % contents['ip'])
            continue

    def show_vmware_info(self):
        script = 'grep -v password %s | grep -v vmwareId' % self.file_path
        result = self.run_cmd(script)
        if not result:
            print("No VMware users.")
            exit(0)
        print(result)

    def usage(self):
        print('''usage: python vmware_tool.py {add|delete|update|display}''')

if __name__ == '__main__':
    manager = VmwareUser()
    if len(argv) !=2:
        manager.usage()
        exit(1)
    try:
        if argv[1] == "add":
            if manager.vmware_count("username") > VCENTER_SUM - 1:
                print("The number of vcenter accounts has reached the limit.")
                exit(0)
            else:
                manager.add_vmware_info()
        elif argv[1] == "delete":
            manager.delete_vmware_info()
        elif argv[1] == "update":
            manager.update_vmware_info()
        elif argv[1] == "display":
            manager.show_vmware_info()
        else:
            manager.usage()
    except KeyboardInterrupt:
        print('\nTerminated.')
        exit(1)
    exit(0)
