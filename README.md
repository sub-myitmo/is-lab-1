# is-lab-1

## Локальное тестирование

0. Указать в терминале переменные окружения:
      ```
      export DB_USERNAME="s409331"
      export DB_PASSWORD="s409331"
      export DB_NAME="studs"
      export DB_HOST="localhost"
      export DB_PORT="5432"
      export DB_TYPE="update"
      ```
1. Поднять бд + миграции (чтобы не вбивать данные вручную): `docker-compose up -d`
2. Запустить фронт: `npm run build` + `npm run dev`
3. Запустить бэк:
    - Скачать wildfly с версией 30+
    - Поменять порты в `standalone.xml`
      ```
      <socket-binding name="ajp" port="${jboss.ajp.port:20567}"/>
      <socket-binding name="http" port="${jboss.http.port:20568}"/>
      <socket-binding name="https" port="${jboss.https.port:20569}"/>
      <socket-binding name="management-http" interface="management" port="${jboss.management.http.port:20570}"/>
      <socket-binding name="management-https" interface="management" port="${jboss.management.https.port:20571}"/>
      ```
    - Запустить командой `./bin/standalone.sh`

## Продовое поднятие (helios)

0. Не забываем про переменные окружения.
1. Поднятие фронта: [гайд](https://github.com/timur1516/is-labs/tree/main?tab=readme-ov-file#%D0%BA%D0%B0%D0%BA-%D0%B7%D0%B0%D0%BF%D1%83%D1%81%D1%82%D0%B8%D1%82%D1%8C)
2. Поднятие бэка:
    - Если вы используете бд не напрямую из кода: [гайд](https://github.com/timur1516/is-labs/tree/main?tab=readme-ov-file#%D0%BA%D0%B0%D0%BA-%D0%B7%D0%B0%D0%BF%D1%83%D1%81%D1%82%D0%B8%D1%82%D1%8C)
    - Если подключаетесь к бд прямо в коде: просто скачайте wildfly на сервер, настройте порты (как в локальном тестировании, при этом порты в конфиге должны совпадать с standalone.xml), запускайте бэк `DB_TYPE="create"`