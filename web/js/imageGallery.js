
let gallery = document.querySelector(".gallery");
let galleryImg = document.querySelector(".gallery_inner img");
let closeBtn = document.querySelector(".close");
let prev = document.querySelector(".control_prev");
let after = document.querySelector(".control_after");
let currentIndex = 0;

document.getElementById("imageInput-add").addEventListener("change", function (event) {
    handleImagePreview(event, "imagePreviewWrapper-add");
});

document.getElementById("imageInput").addEventListener("change", function (event) {
    handleImagePreview(event, "imagePreviewWrapper");
});

function handleImagePreview(event, wrapperId) {
    const files = event.target.files;
    const wrapper = document.getElementById(wrapperId);
    wrapper.innerHTML = ""; // Xóa ảnh cũ

    let loadedCount = 0;
    const newImages = [];

    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (!file.type.startsWith("image/"))
            continue;

        const reader = new FileReader();
        reader.onload = function (e) {
            const img = document.createElement("img");
            img.src = e.target.result;
            img.className = "images_img";

            const div = document.createElement("div");
            div.className = "images";
            div.appendChild(img);
            wrapper.appendChild(div);

            newImages.push(img);
            loadedCount++;

            if (loadedCount === files.length) {
                applyImageTransforms(wrapperId);
                updateImagesAndEvents(wrapperId); // Cập nhật sau khi load xong
            }
        };
        reader.readAsDataURL(file);
    }
}


function applyImageTransforms(wrapperId = null) {
    const imageBlocks = (wrapperId ?
            document.querySelectorAll(`#${wrapperId} .images`) :
            document.querySelectorAll('.images'));

    imageBlocks.forEach((el, index) => {
        if (index < 3) {
            el.style.transform = `translateX(${index * 30}px)`;
            el.style.zIndex = 999 - index;
        } else {
            el.style.transform = `translateX(${2 * 30}px)`;
            el.style.zIndex = 3;
        }
    });
}


function updateImagesAndEvents(wrapperId = null) {
    const images = (wrapperId ?
            document.querySelectorAll(`#${wrapperId} .images_img`) :
            document.querySelectorAll(".images_img"));

    images.forEach((img, index) => {
        img.addEventListener("click", function () {
            currentIndex = index;
            showGallery(images);
        });
    });

    // Gán lại sự kiện điều hướng
    if (prev && after) {
        prev.onclick = () => {
            if (currentIndex > 0) {
                currentIndex--;
                showGallery(images);
            }
        };
        after.onclick = () => {
            if (currentIndex < images.length - 1) {
                currentIndex++;
                showGallery(images);
            }
        };
    }

    // Phím điều hướng
    document.onkeydown = function (e) {
        if (!gallery.classList.contains("show"))
            return;

        if (e.key === "Escape")
            gallery.classList.remove("show");
        if (e.key === "ArrowLeft" && currentIndex > 0) {
            currentIndex--;
            showGallery(images);
        }
        if (e.key === "ArrowRight" && currentIndex < images.length - 1) {
            currentIndex++;
            showGallery(images);
        }
    };
}


// Hiển thị ảnh lớn
function showGallery(images) {
    if (!gallery || !galleryImg || !images.length)
        return;

    galleryImg.src = images[currentIndex].src;
    gallery.classList.add("show");

    // Ẩn/hiện nút
    prev.style.display = currentIndex === 0 ? "none" : "block";
    after.style.display = currentIndex === images.length - 1 ? "none" : "block";
}

// Đóng gallery
if (closeBtn) {
    closeBtn.addEventListener("click", function () {
        gallery.classList.remove("show");
    });
}
