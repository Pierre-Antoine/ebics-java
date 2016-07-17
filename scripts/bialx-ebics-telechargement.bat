:: **********************************************************************
:: Auteur : Pierre-Antoine MARC
:: Script : bialx-ebics-telechargement.bat
:: Paramètres :
:: -D : Indique que l'on va procéder à un téléchargement de fichier
:: -t (facultatif) : Activation du mode TEST du protocole
:: -h : le paramètre host ID
:: -u : le paramètre user ID
:: -p : le paramètre partner ID
:: -f : Format du fichier à récupérer
:: -s (facultatif) : Date de début
:: -e (facultatif) : Date de fin
:: -o : Nom du fichier de sortie
:: **********************************************************************

java -jar bialx-ebics-1.0.jar -D -t -h <HOST_ID> -u <USER_ID> -p <PARTNER_ID> -f <FORMAT> -s <DEBUT> -e <FIN> -o <FICHIER>