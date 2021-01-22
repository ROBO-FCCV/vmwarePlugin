#!/bin/bash
#
# Copyright (c). 2021-2021. All rights reserved.
#

JAVA_OPTS="$JAVA_OPTS -Dorg.apache.catalina.connector.RECYCLE_FACADES=true"
JAVA_OPTS="$JAVA_OPTS -Dorg.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH=false"
JAVA_OPTS="$JAVA_OPTS -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=false"
JAVA_OPTS="$JAVA_OPTS -Dorg.apache.catalina.security.SecurityListener.UMASK=`umask`"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=sercurity"
JAVA_OPTS="$JAVA_OPTS -Dlog.path=/var/log/plugin/vmware_plugin"
JAVA_OPTS="$JAVA_OPTS -server -Xms512m -Xmx512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
JAVA_OPTS="$JAVA_OPTS -Dserver.port=19091"
CATALINA_PID="$CATALINA_BASE/tomcat.pid"
