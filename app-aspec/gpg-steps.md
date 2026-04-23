1. install GPG
```shell
brew install gnupg
```

2. verify installation
```shell
gpg --version
```

3. generate a key
```shell
gpg --full-generate-key
```

- note: 0 means no expiration, 
- note: this is the command for deleting a key if you mess up: `gpg --delete-secret-keys <key-id> && gpg --delete-keys <key-id>`
- note: this is the command for listing keys: `gpg --list-secret-keys --keyid-format LONG`

4. export the private key - used for GitHub Action secrets
```shell
gpg --armor --export-secret-keys <key-id> > private.key
``` 

5. upload public key 
```shell
gpg --keyserver hkps://keys.openpgp.org --send-keys <key-id>
```

6. verify the key is on the server by searching for it:
```shell
gpg --keyserver hkps://keys.openpgp.org --search-keys <key-id>
```




