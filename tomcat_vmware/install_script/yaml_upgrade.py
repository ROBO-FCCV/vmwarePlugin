#  Copyright (c). 2021-2021. All rights reserved.
import os
from sys import argv
import yaml
import kmc.kmc as kmc_tools
encrpy_jar = "crypt-tool-jar-with-dependencies.jar"


def change_vmware_yaml(file_path):
    vmware_array = {}
    with open(file_path, 'r') as load_file:
        load_dict = yaml.safe_load(load_file)
        if load_dict is None:
            return gen_vmware_file(file_path, dict())
        for item in load_dict:
            vmware_id = load_dict[item].get("vmwareId")
            cur_password = kmc_tools.API().decrypt(
                0, load_dict[item].get("password"))
            vmware = {
                'id': load_dict[item].get("vmwareId"),
                'ip': load_dict[item].get("ip"),
                'username': load_dict[item].get("username"),
                'port': 443,
                'password': cur_password
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
        change_vmware_yaml(vmware_yaml)
        change_login_to_user_yaml(login_yaml)
