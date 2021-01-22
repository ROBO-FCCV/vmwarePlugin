#!/usr/bin/env bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

function check_link_full_path()
{
    local in_path=$1
    local temp_path=${in_path}
    while [[ ${temp_path} != "/" ]]; do
        if [[ ! -e ${temp_path} ]]; then
            return 1
        fi
        if [[ -h ${temp_path} ]]; then
            return 1
        fi
        temp_path=$(dirname "${temp_path}")
    done
    return 0
}
function check_link_current_path()
{
    local in_path=$1
    # 当前路径或者文件是否存在
    if [[ ! -e ${in_path} ]]; then
        return 1
    fi
    # 当前文件是否链接
    if [[ -h ${in_path} ]]; then
        return 1
    fi
    return 0
}
