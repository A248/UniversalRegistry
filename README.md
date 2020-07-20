# Omnibus
A powerful framework for events, resource registrations, and multi-threaded utilities.

[![GitHub last commit](https://img.shields.io/github/last-commit/A248/Omnibus.svg)](https://github.com/A248/Omnibus/commits/master)[![Issue Resolution Time](http://isitmaintained.com/badge/resolution/A248/Omnibus.svg)](http://isitmaintained.com/project/A248/Omnibus "Average time to resolve an issue")[![Open Issues](http://isitmaintained.com/badge/open/A248/Omnibus.svg)](http://isitmaintained.com/project/A248/Omnibus "Percentage of issues still open")[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0-standalone.html)

## Dependency

The `space.arim.omnibus:omnibus-all` artifact has transitive dependencies on all the components of Omnibus.

The repository URL is `https://www.arim.space/maven/`.

For convenience, all modules are shaded in `omnibus-all-shaded`.

### Maven

With maven, this would be applied as follows.

Dependency:

```xml
<dependency>
	<groupId>space.arim.omnibus</groupId>
	<artifactId>omnibus-all</artifactId>
	<version>{INSERT_VERSION}</version>
</dependency>
```

Repository:

``` xml
<repository>
	<id>arim-repo</id>
	<url>https://www.arim.space/maven/</url>
</repository>
```

## License

See the file LICENSE.txt for the full GNU General Public License v3. The license applies to this entire repository and is copied into binaries as LICENSE.txt.
