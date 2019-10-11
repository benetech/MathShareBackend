FROM adoptopenjdk/maven-openjdk10 as maven
LABEL maintainer="johnh@benetech.org"

COPY ./pom.xml ./pom.xml

# build all dependencies
RUN mvn dependency:go-offline -B

# copy your other files
COPY ./src ./src
COPY ./checkstyle ./checkstyle

# build for release
RUN mvn package

# our final base image
FROM node:8.9.3-alpine

ARG NODE_ENV=production
ENV NODE_ENV=$NODE_ENV

# Install JDK
RUN apk --update add --no-cache --virtual .build-deps curl binutils supervisor \
    && GLIBC_VER="2.28-r0" \
    && ALPINE_GLIBC_REPO="https://github.com/sgerrand/alpine-pkg-glibc/releases/download" \
    && GCC_LIBS_URL="https://archive.archlinux.org/packages/g/gcc-libs/gcc-libs-8.2.1%2B20180831-1-x86_64.pkg.tar.xz" \
    && GCC_LIBS_SHA256=e4b39fb1f5957c5aab5c2ce0c46e03d30426f3b94b9992b009d417ff2d56af4d \
    && ZLIB_URL="https://archive.archlinux.org/packages/z/zlib/zlib-1%3A1.2.9-1-x86_64.pkg.tar.xz" \
    && ZLIB_SHA256=bb0959c08c1735de27abf01440a6f8a17c5c51e61c3b4c707e988c906d3b7f67 \
    && curl -Ls https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub -o /etc/apk/keys/sgerrand.rsa.pub \
    && curl -Ls ${ALPINE_GLIBC_REPO}/${GLIBC_VER}/glibc-${GLIBC_VER}.apk > /tmp/${GLIBC_VER}.apk \
    && apk add /tmp/${GLIBC_VER}.apk \
    && curl -Ls ${GCC_LIBS_URL} -o /tmp/gcc-libs.tar.xz \
    && echo "${GCC_LIBS_SHA256}  /tmp/gcc-libs.tar.xz" | sha256sum -c - \
    && mkdir /tmp/gcc \
    && tar -xf /tmp/gcc-libs.tar.xz -C /tmp/gcc \
    && mv /tmp/gcc/usr/lib/libgcc* /tmp/gcc/usr/lib/libstdc++* /usr/glibc-compat/lib \
    && strip /usr/glibc-compat/lib/libgcc_s.so.* /usr/glibc-compat/lib/libstdc++.so* \
    && curl -Ls ${ZLIB_URL} -o /tmp/libz.tar.xz \
    && echo "${ZLIB_SHA256}  /tmp/libz.tar.xz" | sha256sum -c - \
    && mkdir /tmp/libz \
    && tar -xf /tmp/libz.tar.xz -C /tmp/libz \
    && mv /tmp/libz/usr/lib/libz.so* /usr/glibc-compat/lib \
    && set -eux; \
    apk add --virtual .fetch-deps curl; \
    ARCH="$(apk --print-arch)"; \
    case "${ARCH}" in \
    amd64|x86_64) \
    ESUM='f8caa2e8c28370e3b8e455686e1ddeb74656f068848f8c355d9d8d1c225528f4'; \
    BINARY_URL='https://github.com/AdoptOpenJDK/openjdk10-releases/releases/download/jdk-10.0.2%2B13/OpenJDK10_x64_Linux_jdk-10.0.2%2B13.tar.gz'; \
    ;; \
    *) \
    echo "Unsupported arch: ${ARCH}"; \
    exit 1; \
    esac; \
    curl -Lso /tmp/openjdk.tar.gz ${BINARY_URL}; \
    sha256sum /tmp/openjdk.tar.gz; \
    mkdir -p /opt/java/openjdk; \
    cd /opt/java/openjdk; \
    echo "${ESUM}  /tmp/openjdk.tar.gz" | sha256sum -c -; \
    tar -xf /tmp/openjdk.tar.gz; \
    jdir=$(dirname $(dirname $(find /opt/java/openjdk -name javac))); \
    mv ${jdir}/* /opt/java/openjdk; \
    apk del --purge .fetch-deps; \
    rm -rf /var/cache/apk/*; \
    rm -rf ${jdir} /tmp/openjdk.tar.gz

ENV JAVA_VERSION jdk-10.0.2+13

ENV JAVA_HOME=/opt/java/openjdk \
    PATH="/opt/java/openjdk/bin:$PATH"

# set deployment directory
WORKDIR /mathshare

# copy over the built artifact from the maven image
COPY --from=maven target/mathshare-*.jar ./app.jar

# Set a working directory
WORKDIR /usr/src/app

# Install Node.js dependencies
COPY gateway/package.json gateway/yarn.lock ./
RUN touch /usr/src/app/supervisord.log && chown -R node:node /usr/src/app/supervisord.log;
RUN set -ex; \
    if [ "$NODE_ENV" = "production" ]; then \
    yarn install --no-cache --frozen-lockfile --production; \
    elif [ "$NODE_ENV" = "test" ]; then \
    touch yarn-error.log; \
    mkdir -m 777 build; \
    yarn install --no-cache --frozen-lockfile; \
    chown -R node:node build node_modules package.json yarn.lock yarn-error.log; \
    else \
    touch yarn-error.log; \
    mkdir -p -m 777 build node_modules /home/node/.cache/yarn; \
    chown -R node:node build node_modules package.json yarn.lock yarn-error.log /home/node/.cache/yarn; \
    fi;

# Copy application files
COPY gateway/tools ./tools/
COPY gateway/migrations ./migrations/
COPY gateway/seeds ./seeds/
COPY gateway/locales ./locales/
COPY gateway/src ./src
COPY gateway/tools ./tools
COPY gateway/.babelrc ./.babelrc
COPY gateway/.eslintrc.js ./.eslintrc.js
COPY gateway/.gitignore ./.gitignore
COPY gateway/.prettierrc ./.prettierrc
COPY gateway/package.json ./package.json
COPY gateway/yarn.lock ./yarn.lock

ADD supervisord.conf /etc/
RUN yarn install --production=false && node tools/build.js
RUN printenv

EXPOSE 8080

ENTRYPOINT ["supervisord", "--nodaemon", "--configuration", "/etc/supervisord.conf"]
