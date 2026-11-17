
allow_k8s_contexts('polar')

custom_build(
    ref='catalog-service',
    command='./gradlew bootBuildImage --imageName $EXPECTED_REF && minikube image load $EXPECTED_REF --profile polar',
    deps=['build.gradle', 'src'],
    skips_local_docker=True,
)

k8s_yaml(['k8s/deployment.yml', 'k8s/service.yml'])

k8s_resource('catalog-service', port_forwards=['9001'])
