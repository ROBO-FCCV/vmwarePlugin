#  Copyright (c). 2021-2021. All rights reserved.
import os
from sys import argv
import yaml
import subprocess
import kmc.kmc as kmc_tools
encrpy_jar = "crypt-tool-jar-with-dependencies.jar"


def change_vmware_yaml(file_path, in_jar_path):
    current_path = os.path.abspath(os.path.dirname(__file__))
    jar_path = os.path.join(
        os.path.dirname(in_jar_path), "%s" % encrpy_jar)
    en_file = os.path.join(
        current_path, "temp_encrpy_file.txt")
    vmware_array = {}
    with open(file_path, 'r') as load_file:
        load_dict = yaml.safe_load(load_file)
        if load_dict is None:
            return gen_vmware_file(file_path, dict())
        for item in load_dict:
            vmware_id = load_dict[item].get("vmwareId")
            cur_password = kmc_tools.API().decrypt(
                0, load_dict[item].get("password"))
            if os.path.exists(en_file):
                os.remove(en_file)
            with open(en_file, "w") as temp_file:
                temp_file.write("")
            cmd = "export LD_LIBRARY_PATH=/opt/huawei/robo/kmc/lib;" \
                  "echo %s|java -cp %s " \
                  "com.huawei.robo.crypt.tool.Application " \
                  "-vmware 0 %s" % (cur_password, jar_path, en_file)
            (status, output) = subprocess.getstatusoutput(cmd)
            if status != 0:
                print(output)
                exit(1)
            with open(en_file, "r") as temp_file:
                new_pwd = temp_file.read()
            vmware = {
                'id': load_dict[item].get("vmwareId"),
                'ip': load_dict[item].get("ip"),
                'username': load_dict[item].get("username"),
                'port': 443,
                'password': "encrypt(%s)" % new_pwd
            }
            vmware_array[vmware_id] = vmware
    gen_vmware_file(file_path, vmware_array)


def gen_vmware_file(file_path, vmware_array):
    vmware_conf = {
        'vmware': {
            'configs': vmware_array
        }
    }
    new_file_name = os.path.dirname(file_path) + '/vmware.yml'
    if os.path.exists(new_file_name):
        os.remove(new_file_name)
    with open(new_file_name, 'w') as dump_file:
        dump_file.write(yaml.dump(vmware_conf))


def change_login_to_user_yaml(file_path):
    with open(file_path, "r") as old_file:
        old_data = yaml.safe_load(old_file)
    vmware = {
        'vmware': {
            'user': {
                'roles': ['admin'],
                'empire': 'PT30m',
                'username': '%s' % old_data["username"],
                'password': '%s' % old_data["password"]
            }
        }
    }
    new_file_name = os.path.dirname(file_path) + '/user.yml'
    if os.path.exists(new_file_name):
        os.remove(new_file_name)
    with open(new_file_name, 'w')as dump_file:
        dump_file.write(yaml.dump(vmware))


if __name__ == '__main__':
    if len(argv) != 4:
        print('Error input')
        exit(1)
    else:
        vmware_yaml = argv[1]
        login_yaml = argv[2]
        en_jar_path = argv[3]
        change_vmware_yaml(vmware_yaml, en_jar_path)
        change_login_to_user_yaml(login_yaml)
