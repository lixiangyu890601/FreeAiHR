<template>
    <div class="header-content">
        <h2>人才招聘系统</h2>
        <div class="user-info">
            <div class="user-avatar">
                <el-avatar :size="40" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            </div>
            <span class="username">admin</span>
        </div>
    </div>
</template>

<script setup>
</script>

<style scoped>
.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 80px;
    background-color: #fff;
    padding: 0 30px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    z-index: 1000;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.username {
    font-size: 16px;
    color: #333;
    font-weight: 500;
}
</style>