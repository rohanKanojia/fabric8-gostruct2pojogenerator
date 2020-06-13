module github.com/rohanKanojia/code-generation-using-fabric8

go 1.14

require (
	github.com/Kong/kubernetes-ingress-controller v0.0.5 // indirect

	github.com/google/go-containerregistry v0.0.0-20200413145205-82d30a103c0a // indirect
	github.com/knative/eventing v0.13.2
	github.com/knative/serving v0.13.2
	github.com/robfig/cron v1.2.0 // indirect
	go.uber.org/zap v1.14.1 // indirect
	gomodules.xyz/jsonpatch/v2 v2.1.0 // indirect
	k8s.io/api v0.17.4 // indirect
	k8s.io/apimachinery v0.17.4
	k8s.io/client-go v0.17.4 // indirect
	knative.dev/eventing v0.13.2
	knative.dev/pkg v0.0.0-20200306230727-a56a6ea3fa56
	knative.dev/serving v0.13.2 // indirect

)

exclude (
	k8s.io/api v0.0.0
	k8s.io/apiextensions-apiserver v0.0.0
	k8s.io/apimachinery v0.0.0
	k8s.io/apiserver v0.0.0
	k8s.io/cli-runtime v0.0.0
	k8s.io/client-go v0.0.0
	k8s.io/cloud-provider v0.0.0
	k8s.io/cluster-bootstrap v0.0.0
	k8s.io/code-generator v0.0.0
	k8s.io/component-base v0.0.0
	k8s.io/cri-api v0.0.0
	k8s.io/csi-translation-lib v0.0.0
	k8s.io/kube-aggregator v0.0.0
	k8s.io/kube-controller-manager v0.0.0
	k8s.io/kube-proxy v0.0.0
	k8s.io/kube-scheduler v0.0.0
	k8s.io/kubectl v0.0.0
	k8s.io/kubelet v0.0.0
	k8s.io/legacy-cloud-providers v0.0.0
	k8s.io/metrics v0.0.0
	k8s.io/sample-apiserver v0.0.0
)
