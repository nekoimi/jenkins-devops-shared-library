#!/usr/bin/env bash

bash -ex;

deployStatus=\$(helm list --all --time-format "2006-01-02" --filter "${MY_PROJECT_NAME}" | sed -n '2p' | awk '{print \$5}')

if [ deployStatus == 'deployed' ]; then
  # upgrade
else
  # install
fi

if [ -f $file ]; then

fi
