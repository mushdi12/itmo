.PHONY: all build test ctest bench clean

BUILD_DIR ?= build
BUILD_TYPE ?= Release
CTEST_FLAGS ?= --output-on-failure

all: build

$(BUILD_DIR)/Makefile:
	cmake -S . -B $(BUILD_DIR) -DCMAKE_BUILD_TYPE=$(BUILD_TYPE)

build: $(BUILD_DIR)/Makefile
	cmake --build $(BUILD_DIR) -j

test: build
	ctest --test-dir $(BUILD_DIR)/test $(CTEST_FLAGS)

ctest: test

bench: build
	@echo "Собраны исполняемые файлы для бенчмаркинга:"
	@echo "  ./$(BUILD_DIR)/bench/io-load"
	@echo "  ./$(BUILD_DIR)/bench/vtpc_loader_optimal"

clean:
	rm -rf $(BUILD_DIR)
