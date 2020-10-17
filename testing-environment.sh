#!/bin/bash
if [ "$RUNTIME" = "" ];
then
    export RUNTIME="`which podman`"
    if [ "$RUNTIME" = "" ];
    then
        export RUNTIME="`which docker`"
    fi
fi

echo "Use runtime $RUNTIME"

function stop {
    echo "Stop!"
    $RUNTIME stop  jmestore-testing-mysql  ||true
    $RUNTIME stop  jmestore-testing-pma  ||true
    $RUNTIME stop jmestore-testing-store  ||true
    $RUNTIME rm   jmestore-testing-mysql   ||true
    $RUNTIME rm   jmestore-testing-pma ||true
    $RUNTIME rm   jmestore-testing-store ||true
    $RUNTIME network rm jmestore_testing_net|| true

}


function start {
    stop    
    echo "Start!"
    mkdir -p "$PWD/test_environment"
    mkdir -p "$PWD/test_environment/mysql"
    $RUNTIME network create -d bridge   jmestore_testing_net

    $RUNTIME run --rm  -d   \
        --name jmestore-testing-mysql \
        -v "$PWD/test_environment/mysql":/var/lib/mysql \
        -e MYSQL_ROOT_PASSWORD="oEZi1nIeZwpS"  \
        -p 3306:3306 \
        mariadb 
    $RUNTIME network connect --alias  mysql jmestore_testing_net  jmestore-testing-mysql   

    $RUNTIME run -d --rm --name jmestore-testing-pma \
    -e PMA_HOST=jmestore-testing-mysql  \
    -p 8081:80 \
    phpmyadmin/phpmyadmin
    $RUNTIME network connect --alias  phpmyadmin.mysql jmestore_testing_net  jmestore-testing-pma   


    if [ "$RUN_STORE" != "" ];
    then
        mkdir -p "$PWD/test_environment/store_config"
        mkdir -p "$PWD/test_environment/store_images"

        rm -f "$PWD/test_environment/store_config/server-config.json" || true
        cp "config/test-environment-config.json" "$PWD/test_environment/store_config/server-config.json"
    
        $RUNTIME create --rm --name jmestore-testing-store \
            --read-only \
            -v"$PWD/test_environment/store_config":/app/config \
            -v"$PWD/test_environment/store_images":/app/www/images/database \
            --tmpfs  /app/sitemap \
            --tmpfs  /tmp/apptmp \
            -p 8080:8080 \
            jmestore

        $RUNTIME network connect --alias  store.docker jmestore_testing_net  jmestore-testing-store   

        $RUNTIME start  jmestore-testing-store
        $RUNTIME logs --follow  jmestore-testing-store
    fi
}

$1