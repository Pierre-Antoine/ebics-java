:: **********************************************************************
:: Auteur : Pierre-Antoine MARC
:: Script : bialx-ebics-creation.bat
:: Paramètres :
:: - C : Indique que l'on va procéder à une création d'utilisateur
:: - h : le paramètre host ID
:: - u : le paramètre user ID
:: - b : l'URL de la banque
:: - B : le nom de la banque
:: - p : le paramètre partner ID
:: **********************************************************************

java -jar %~dp0bialx-ebics-1.0.jar -C -h <HOST_ID> -u <USER_ID> -b <URL_BANQUE_EBICS> -B <NOM_BANQUE> -p <PARTNER_ID>