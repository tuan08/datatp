#Add User

Create user

```
sudo useradd -m -d /home/datatp  -c "datatp user" datatp  -s /bin/bash 
```

Change password
```
passwd datatp
```

Add sudo privilege 

```
usermod -aG sudo datatp
```

