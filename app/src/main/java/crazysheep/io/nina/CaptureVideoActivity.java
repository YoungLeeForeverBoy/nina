package crazysheep.io.nina;

import android.Manifest;
import android.os.Bundle;

import java.util.List;

import crazysheep.io.nina.compat.APICompat;
import crazysheep.io.nina.constants.PermissionConstants;
import crazysheep.io.nina.fragment.Camera2CaptureVideoFragment;
import crazysheep.io.nina.utils.ToastUtils;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * capture video activity
 *
 * Created by crazysheep on 16/3/17.
 */
public class CaptureVideoActivity extends BaseActivity {

    private final String[] PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_video);

        // request camera permission
        if(EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            initUI();
        } else {
            EasyPermissions.requestPermissions(this, null, PermissionConstants.RC_CAMERA,
                    PERMISSIONS);
        }
    }

    @Override
    public void onPermissionsGranted(List<String> perms) {
        super.onPermissionsGranted(perms);

        initUI();
    }

    @Override
    public void onPermissionsDenied(List<String> perms) {
        super.onPermissionsDenied(perms);

        ToastUtils.t(this, getString(R.string.camera_permission_denied));
        finish();
    }

    private void initUI() {
        if(APICompat.api21()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_ft, new Camera2CaptureVideoFragment(),
                            Camera2CaptureVideoFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

}
