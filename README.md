<table>
  <tr><td colspan="4"><h1>I. Giới thiệu chung về ứng dụng</h1></td></tr>
  <tr>
    <td colspan="4">
      1. Mục tiêu và công nghệ sử dụng<br>
      - Ứng dụng là một ứng dụng di động giả lập dịch vụ đặt đồ ăn nhanh, được phát triển trên nền tảng Android (Java) và tuân theo kiến trúc Model-View-ViewModel (MVVM).
      Ứng dụng được thiết kế nhằm cung cấp cho người dùng một trải nghiệm đặt hàng nhanh chóng, tiện lợi và hiệu quả.
    </td>
  </tr>
  <tr>
    <td colspan="4">
      * Công nghệ sử dụng: <br>
      - Backend: Google Firebase Firestore (Database), Firebase Authentication (Xác thực), Cloudinary (Lưu trữ ảnh). <br>
      - Kiến trúc: MVVM (LiveData, ViewModel, Repository Pattern). <br>
      - Giao diện: ViewPager2 (Quản lý các tab chính), RecyclerView (Hiển thị danh sách), ConstraintLayout (Bố cục phẳng). <br>
      - Xử lý ảnh: Thư viện Glide (Tối ưu hóa tải và hiển thị ảnh). <br>
    </td>
  </tr>
  <tr>
    <td colspan="4">
      2. Các thành phần dữ liệu cốt lõi (POJOs)<br>
      - Dữ liệu được tổ chức tại các Root Collection và Sub-collection: <br>
       <ul>
          <li><b>UserItem</b>: Lưu trữ hồ sơ người dùng.</li>
          <li><b>FoodItem</b>: Thông tin sản phẩm.</li>
          <li><b>CategoryItem</b>: Danh mục sản phẩm.</li>
          <li><b>NotificationItem</b>: Thông báo hệ thống.</li>
          <li><b>CartItem</b>: Mô hình cho các món hàng trong Giỏ hàng (dùng để cache dữ liệu).</li>
          <li><b>AddressItem</b>: Mô hình cho địa chỉ giao hàng (dùng để quản lý Địa chỉ Mặc định).</li>
          <li><b>OrderItem</b>: Mô hình cho đơn hàng đã đặt (lưu trữ tóm tắt và mảng items đã đặt).</li>
       </ul>
    </td>
  </tr>
  <tr>
    <td colspan="4">
      3. Các chức năng chính<br>
       - Đăng nhập, Đăng ký, Đăng xuất, Quên mật khẩu, Đổi mật khẩu, Xóa tài khoản, Chỉnh sửa thông tin tài khoản. <br>
       - Xem chi tiết sản phẩm, Xem sản phẩm theo danh mục, Tìm kiếm sản phẩm, Đặt hàng. <br>
       - Quản lý sản phẩm yêu thích, Quản lý giỏ hàng, Quản lý địa chỉ giao hàng, Quản lý thông báo, Quản lý lịch sử đặt hàng. <br>
    </td>
  </tr>
  <tr><td colspan="4"><h1>II. Giao diện ứng dụng</h1></td></tr>
  <tr>
    <td colspan="4">1. Giao diện Đăng nhập, Đăng ký, Quên mật khẩu</td>
  </tr>
   <tr>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_115256" src="https://github.com/user-attachments/assets/aa327e0d-3bd7-4b9f-88db-de2fb415f5b7" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114552" src="https://github.com/user-attachments/assets/d8533ae2-d354-4efa-ba1d-34ee03e661fb" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_115307" src="https://github.com/user-attachments/assets/d7ccc200-b3c3-4678-b193-68260f5eabba" /></td>
    </tr>
   <tr>
    <td colspan="4">2. Giao diện trang chủ, thực đơn, giỏ hàng, tài khoản</td>
  </tr>
   <tr>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114315" src="https://github.com/user-attachments/assets/e4206d40-9418-4ca8-9a5b-9c95930fcf7e" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114443" src="https://github.com/user-attachments/assets/358fe6b6-940f-406f-8dc0-cdf291a904a9" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114510" src="https://github.com/user-attachments/assets/0bc43c7f-3bc0-46ec-9b91-9b136358d537" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114541" src="https://github.com/user-attachments/assets/9fbb01ea-250d-4590-8541-8eb3419c4488" /></td>
    </tr>
   <tr>
    <td colspan="4">3. Giao diện yêu thích, thông báo, thanh toán </td>
  </tr>
   <tr>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114918" src="https://github.com/user-attachments/assets/6977bd56-9208-4af6-b36c-cc9352cc4063" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114611" src="https://github.com/user-attachments/assets/8cc6cc0a-6bfd-43b0-921c-02155ee899da" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114523" src="https://github.com/user-attachments/assets/f34a1bac-72cb-499c-b1e0-510a3cc43038" /></td>
    </tr>
   <tr>
    <td colspan="4">4. Giao diện lịch sử đặt hàng, chi tiết lịch sử đặt hàng </td>
  </tr>
   <tr>
      <td><img width="1344" height="2992" alt="Screenshot_20251117_124529" src="https://github.com/user-attachments/assets/72aef55b-f50a-4a23-a354-5a8b502329d7" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251117_124554" src="https://github.com/user-attachments/assets/a9414392-73a5-4004-b6a2-6475dd78d4b9" /></td>
    </tr>
   <tr>
    <td colspan="4">5. Giao diện quản lý địa chỉ giao hàng </td>
  </tr>
   <tr>
      <td><img width="1344" height="2992" alt="Screenshot_20251117_150620" src="https://github.com/user-attachments/assets/18d0c99a-b40c-47cd-8b1a-b8808a871d88" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114633" src="https://github.com/user-attachments/assets/81accad2-02e9-47bd-8785-2fa9b73b6516" /></td>
    </tr>
   <tr>
    <td colspan="4">6. Giao diện xem chi tiết sản phẩm, xem sản phẩm theo danh mục, tìm kiếm sản phẩm</td>
  </tr>
   <tr>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114904" src="https://github.com/user-attachments/assets/c808e71a-f358-4b99-bf66-cd8977ed5284" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114935" src="https://github.com/user-attachments/assets/bca6532d-7169-47da-b572-2070f050504d" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_115012" src="https://github.com/user-attachments/assets/f07f5ba2-c493-4b27-a3ad-ec8e5e366d25" /></td>
    </tr>
    <tr>
    <td colspan="4">7. Giao diện chỉnh sửa thông tin cá nhân, đổi mật khẩu, cài đặt</td>
  </tr>
   <tr>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114552" src="https://github.com/user-attachments/assets/25423dea-c099-488b-90a8-6eb87a32e541" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114643" src="https://github.com/user-attachments/assets/f28994d3-41d0-41f7-bcb1-07183e47337e" /></td>
      <td><img width="1344" height="2992" alt="Screenshot_20251115_114655" src="https://github.com/user-attachments/assets/57f76e5c-69d7-49c1-acbd-8cb7660f8ce2" /></td>
    </tr>
</table>
