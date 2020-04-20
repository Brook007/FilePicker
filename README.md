# FilePicker
Android文件选择器，默认实现了图片选择器，可自行继承并实现任意的文件类型选择器

[![Jitpack](https://jitpack.io/v/Brook007/FilePicker.svg)](https://jitpack.io/#Brook007/FilePicker)

## 引入依赖

1. 根项目的build.gradle中加入以下代码

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

2. 在需要的模块加入以下的依赖

```gradle
dependencies {
	implementation 'com.github.Brook007:FilePicker:1.0.5'
}
```

## 初始化

### 初始化一个默认的配置

通过下面的代码配置一个默认的配置
```java
FilePickerConfig defaultConfig = FilePickerConfig.DEFAULT_CONFIG;
defaultConfig.setImageLoader(new IPreviewImageLoader() {
    @Override
    public void loadPreviewImage(File sourceFile, ImageView previewImageView) {
        Glide.with(previewImageView.getContext())
             .load(sourceFile)
             .apply(new RequestOptions().centerCrop())
             .into(previewImageView);
    }
});
defaultConfig.setPickerCount(9);
```
### 通过链式调用在每次使用的时候创建一个配置

通过下面的代码在每次调用选择器的时候创建一个新的配置，并启动选择器，这个配置不会影响到默认的配置

```java
FilePickerUtils.getInstance()
        .setPickerCount(1)
        .setFilePickerType(FilePickerConfig.Type.ALL)
        .launchPicker(MainActivity.this, new FilePickerValueCallback() {
            @Override
            public void onPickResult(List<File> file) {
                Log.d("TAG", "回调" + Arrays.toString(file.toArray()));
            }
        });
```

## 使用配置

编写下面的代码来使用指定的配置，并启动选择器

```java
FilePickerUtils.getInstance()
        .setPickerCount(1)
        .setFilePickerConfig(config)
        .launchPicker(MainActivity.this, new FilePickerValueCallback() {
            @Override
            public void onPickResult(List<File> file) {
                Log.d("TAG", "回调" + Arrays.toString(file.toArray()));
            }
        });
```
