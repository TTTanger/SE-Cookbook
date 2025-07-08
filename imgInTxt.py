import os
img_dir = "src/main/resources/g/imgs"
with open(os.path.join(img_dir, "images.txt"), "w", encoding="utf-8") as f:
    for name in os.listdir(img_dir):
        if name.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.bmp')):
            f.write(name + "\n")