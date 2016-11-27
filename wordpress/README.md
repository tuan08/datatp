#Install the Nginx Web Server#

More detail at https://www.digitalocean.com/community/tutorials/how-to-install-linux-nginx-mysql-php-lemp-stack-in-ubuntu-16-04

```
sudo apt-get update
sudo apt-get install nginx
```

In Ubuntu 14.04, Nginx is configured to start running upon installation.

You can test if the server is up and running by accessing your server's domain name or public IP address in your web browser.

To test nginx service:

```
http://server_domain_name_or_IP
#Or
http://localhost
```



#Install MySQL to Manage Site Data#

```
sudo apt-get install mysql-server
```

You will be asked to supply a root (administrative) password for use within the MySQL system.

Export a wordpress database
```
mysqldump -u username -p wordpress > wordpress.sql
```

Import a wordpress database
```
mysql -u <username> -p<PlainPassword> wordpress < wordpress.sql
```

#Install PHP for Processing#


```
sudo apt-get install php-fpm php-mysql
```

##Configure the PHP Processor##

Open the main php-fpm configuration file with root privileges:

```
sudo vi /etc/php/7.0/fpm/php.ini
#Look for ;cgi.fix_pathinfo=1 uncomment and change 1 to 0
```

Now, we just need to restart our PHP processor by typing:
```
sudo systemctl restart php7.0-fpm
```

#Configure Nginx to Use the PHP Processor#

```
sudo vi /etc/nginx/sites-available/default

#Modify the file with the content (add index.php, location /, location -)

server {
  listen 80 default_server;
  listen [::]:80 default_server;

  root /var/www/html;
  index index.php index.html index.htm index.nginx-debian.html;

  server_name server_domain_or_IP;

  location / {
    try_files $uri $uri/ =404;
  }

  location ~ \.php$ {
    include snippets/fastcgi-php.conf;
    fastcgi_pass unix:/run/php/php7.0-fpm.sock;
  }

  location ~ /\.ht {
    deny all;
  }
}
```

Test your configuration file for syntax errors by typing:

```
sudo nginx -t
```

When you are ready, reload Nginx to make the necessary changes:

```
sudo systemctl reload nginx
```

#Create a PHP File to Test Configuration#
We can do this by creating a test PHP file in our document root. Open a new file called info.php within your document root in your text editor:

```
sudo vi /var/www/html/info.php
```

Type or paste the following lines into the new file. This is valid PHP code that will return information about our server:

```
<?php
phpinfo();
```

Visit the url

```
http://server_domain_name_or_IP/info.php
```

#Wordpress#


##Install Additional PHP Extensions##

```
sudo apt-get update
sudo apt-get install php-curl php-gd php-mbstring php-mcrypt php-xml php-xmlrpc
```
Restart nginx

```
sudo systemctl reload nginx
```

##Create a MySQL Database and User for WordPress##

Check mysql status 
```
sudo service mysql status
mysql -u root -p
```

Create a MySQL Database and User for WordPress

```
mysql -u root -p
mysql > CREATE DATABASE wordpress DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
mysql > FLUSH PRIVILEGES;
mysql > EXIT;
```

##Download WordPress##

```
curl -O https://wordpress.org/latest.tar.gz
```

Extract the compressed file to create the WordPress directory structure:

```
tar xzvf latest.tar.gz
```

Create .htaccess

```
cd wordpress
touch .htaccess
chmod 660 .htaccess
```

We'll also copy over the sample configuration file to the filename that WordPress actually reads:
```
cd wordpress
cp wp-config-sample.php wp-config.php
```

We can also create the upgrade directory, so that WordPress won't run into permissions issues 
when trying to do this on its own following an update to its software:
```
cd wordpress
mkdir wp-content/upgrade
```

Now, we can copy the entire contents of the directory into our document root. 
```
sudo cp -a wordpress/. /var/www/html
```

Change permissions
```
sudo chown -R tuan:www-data /var/www/html
#We can set the setgid bit on every directory in our WordPress installation by typing:
sudo find /var/www/html -type d -exec chmod g+s {} \;
#There are a few other fine-grained permissions we'll adjust. First, we'll give group write access 
#to the wp-content directory so that the web interface can make theme and plugin changes:
sudo chmod g+w /var/www/html/wp-content
sudo chmod -R g+w /var/www/html/wp-content/themes
sudo chmod -R g+w /var/www/html/wp-content/plugins
```

Now, open the WordPress configuration file:
```
vi /var/www/html/wp-config.php

#Update

define('DB_NAME', 'wordpress');

/** MySQL database username */
define('DB_USER', 'wordpressuser');

/** MySQL database password */
define('DB_PASSWORD', 'password');

```

In your web browser, navigate to your server's domain name or public IP address:
```
http://server_domain_or_IP
```
Continue the web installation
