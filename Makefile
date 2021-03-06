GGC_CORE_PATH=./ggc-core
GGC_APP_PATH=./ggc-app
CLASSPATH=$(shell pwd)/po-uilib/po-uilib.jar:$(shell pwd)/ggc-app/ggc-app.jar:$(shell pwd)/ggc-core/ggc-core.jar

all: compile
	CLASSPATH=$(CLASSPATH) java ggc.app.App
compile::
	$(MAKE) $(MFLAGS) -C $(GGC_CORE_PATH)
	$(MAKE) $(MFLAGS) -C $(GGC_APP_PATH)
clean:
	$(MAKE) $(MFLAGS) -C $(GGC_CORE_PATH) clean
	$(MAKE) $(MFLAGS) -C $(GGC_APP_PATH) clean
