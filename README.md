# Permission-Manager
# This package is used to hanadle permission requests at runtime 
# Isolate it from the context

# How to use :

```
Activity : PermissionManager.from(this, null)    
Fragment : PermissionManager.from(null, this)



PermissionManager.from(this, null)   
    .request(Permission.Location)
    .rationale(R.string.location_permission_request)
    .checkPermission { granted ->   
    
        if (granted) {
            // Permission granted
            // do what you want
                        
        } else {
            // Not granted
        }
}
```
