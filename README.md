# RetoBici Front End

## Instalación Front End

La aplicación se debe instalar en un dispositivo Android con una versión
5.0 (API 21) como mínimo, pero es recomendable por rendimiento utilizar
un dispositivo con la última versión disponible. Junto a esta memoria se
entrega el fichero `retobici.apk`, al descargarse en el dispositivo
móvil, normalmente se ubica en la carpeta *Descargas* del dispositivo,
al ejecutarse el sistema Android pedirá permisos para instalar
aplicaciones externas, tras confirmar la aplicación será instalada.

Hay que detallar ciertos aspectos de la configuración del proyecto
Android que podrían variar dependiendo del entorno. Hay que destacar que
todos estos valores ya se encuentran definidos en la APK y que esta
sección se detalla por si fuera necesario en un futuro.

Sería necesario agregar las claves de Mapbox que se han generado
previamente.

1.  La clave secreta tiene que agregarse en dos ubicaciones

    1. En un fichero de configuración de Gradle, editar o crear si no
       existe el fichero\
       `~/.gradle/gradle.properties` agregando el siguiente contenido\
       `MAPBOX_DOWNLOADS_TOKEN=clave-secreta-mapbox`

    2. En el fichero
       `Retobici/plazalazojorge_2022_frontend/settings.gradle` se tiene
       que agregar el repositorio Mapbox, agregando el siguiente contenido en el ficherp `setting.gradle`
       ```
       repositories {
        google()
        mavenCentral()
        maven {
            url 'https://api.mapbox.com/downloads/v2/releases/maven'
            authentication {
                basic(BasicAuthentication)
            }
            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = "clave-secreta-mapbox"
            }
        }
        maven { url 'https://jitpack.io' }
       }
       ```

2.  La clave pública se agrega en el fichero `res/values/mapbox_access_token.xml`

Por último destacar que para que el Front End se comunique con el Back End es necesario que en el siguiente fichero:
`es/uva/retobici/frontend/core/di/NetworkModule.kt`
en el que se configura *Retrofit*, el cliente HTTP, configurar la misma dirección IP
que la que se use en el Back End. El código sería el siguiente:
```
@Singleton
    @Provides
    fun providesRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://192.168.65.9:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
```
