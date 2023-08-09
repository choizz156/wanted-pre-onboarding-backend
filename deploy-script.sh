#!/bin/bash

txtred='\033[1;31m'
txtlw='\033[1;33m'
txtpur='\033[1;35m'
txtgrn='\033[1;32m'
txtgrey='\033[1;30m'

echo -e "${txtpur} =================================="
echo -e "${txtpur} <<<<<<스크립트>>>>>>"
echo -e "${txtpur}==================================${txtgrn}"

EXECUTION_PATH=$(pwd)
SHELL_SCRIPT_PATH=$(dirname $0)
BRANCH=$1
PROFILE=$2
JAR=$3

echo ""
echo -e "${txtlw} 쉘 경로 : ${SHELL_SCRIPT_PATH}"
echo -e "${txtgrey}실행 경로 : ${EXECUTION_PATH}"
echo -e "${txtpur} 쉘 이름:$0 브랜치 이름 :  $BRANCH ${txtred} 적용 프로파일 : $PROFILE"
echo "$JAR"
echo -e "${txtpur}==============================${txtgrn}"

function check_branch() {

  git fetch
  MASTER=$(git rev-parse $BRANCH)
  REMOTE=$(git rev-parse origin/$BRANCH)

  if [ $MASTER == $REMOTE ]; then
    echo "[$(date)] 할게 없음"
    exit 1
  else
    git diff
  fi
}

function check_pull() {
  if [[ $pull = y ]]; then
    echo "git pull"
    git pull origin $BRANCH --recurse-submodules
  else
    echo "=======exit============"
    exit 1
  fi
}

JAVA_PID=$(pgrep -f java)

function shutdown() {
  kill -9 $JAVA_PID
  sleep 1
  echo -e "${txtpur}============================ ${JAVA_PID} ${txtlw}"
}

function deploy() {
  nohup java -jar -Dspring.profiles.active=${PROFILE} ${JAR} 1> web.log 2>&1 &
  echo -e "${txtgrn}============finish deploying!========$(pgrep -f java)"
}

check_branch

echo -n "do you want git pull (y/else): "
read pull
echo "your answer is $pull"

check_pull
shutdown
deploy
