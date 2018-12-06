package com.bosnet.ngemart.libgen.barcode_scanner.camera;

import com.bosnet.ngemart.libgen.barcode_scanner.SourceData;

/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
