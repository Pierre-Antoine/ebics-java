Java client for ebics protocol
==============================

The code was synced (with svn2git) and mavenfied

Original Project
----------------

http://sourceforge.net/projects/ebics/

Maven
-----

You need to add the following mirror in your ~/.m2/settings.xml otherwise stax could not be resolved.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <mirrors>
        <mirror>
            <id>simexplorer</id>
            <name>SimExplorer repository</name>
            <url>http://maven.simexplorer.org/repo/</url>
            <mirrorOf>central, SE-springsource-release, SE-springsource-external, SE-IN2P3, SEIS-codelutin, SE-nuiton, SE-restlet</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```

Documentation
-------------

```
mvn javadoc:javadoc
```

HTML doc is generated to `target/site/apidocs/`.

Tested bank services
---------------------

<table>
<tr><th>BANK</th><th>COUNTRY</th><th>STATUS</th></tr>
<tr><td>Credit Agricole</td><td>France</td><td>OK</td></tr>
<tr><td>Société Générale</td><td>France</td><td>OK</td></tr>
<tr><td>Le crédit Lyonais</td><td>France</td><td>?</td></tr>
<tr><td>La banque postale</td><td>France</td><td>pending</td></tr>
</table>

Related Links
-------------

* [EBICS official website](http://www.ebics.org/)
* [EBICS Qualification](http://www.qualif-ebics.fr/)
* [LinuxFR news](http://linuxfr.org/news/enfin-un-client-ebics-java-libre)

Fonctionnement
--------------

Pour faire tourner le programme, il faut lancer la ligne de commande suivante :

```
java -cp ebics-1.0.2.jar org.kopi.ebics.test.FULRequestor.java [arguments]
```

Génération des certificats P12
------------------------------

```
openssl pkcs12 -in [NOM_CERTIFICAT] Auth.pfx -nocerts -out [USER_ID]-X002.key -nodes
openssl pkcs12 -in [NOM_CERTIFICAT] Auth.pfx -nokeys -out [USER_ID]-X002.cert

openssl pkcs12 -in [NOM_CERTIFICAT] Crypt.pfx -nocerts -out [USER_ID]-E002.key -nodes
openssl pkcs12 -in [NOM_CERTIFICAT] Crypt.pfx -nokeys -out [USER_ID]-E002.cert

openssl pkcs12 -in [NOM_CERTIFICAT] Sign.pfx -nocerts -out [USER_ID]-A005.key -nodes
openssl pkcs12 -in [NOM_CERTIFICAT] Sign.pfx -nokeys -out [USER_ID]-A005.cert

openssl rsa -in [USER_ID]-X002 -out [USER_ID]-X002
openssl rsa -in [USER_ID]-A005 -out [USER_ID]-A005
openssl rsa -in [USER_ID]-E002 -out [USER_ID]-E002

openssl pkcs12 -export -name [USER_ID]-A005 -in [USER_ID]-A005.cert -inkey [USER_ID]-A005.key -out [USER_ID]-A005.p12
openssl pkcs12 -export -name [USER_ID]-X002 -in [USER_ID]-X002.cert -inkey [USER_ID]-X002.key -out [USER_ID]-X002.p12
openssl pkcs12 -export -name [USER_ID]-E002 -in [USER_ID]-E002.cert -inkey [USER_ID]-E002.key -out [USER_ID]-E002.p12

[JAVA_PATH]/keytool.exe -importkeystore -destkeystore [USER_ID].jks -srckeystore [USER_ID]-E002.p12 -srcstoretype pkcs12 -alias [USER_ID]-E002 -srcstorepass '[MOT DE PASSE]'
[JAVA_PATH]/keytool.exe -importkeystore -destkeystore [USER_ID].jks -srckeystore [USER_ID]-X002.p12 -srcstoretype pkcs12 -alias [USER_ID]-X002 -srcstorepass '[MOT DE PASSE]'
[JAVA_PATH]/keytool.exe -importkeystore -destkeystore [USER_ID].jks -srckeystore [USER_ID]-A005.p12 -srcstoretype pkcs12 -alias [USER_ID]-A005 -srcstorepass '[MOT DE PASSE]'

[JAVA_PATH]/keytool.exe -importkeystore -srckeystore [USER_ID].jks -destkeystore [USER_ID].p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass [MOT DE PASSE]
```

