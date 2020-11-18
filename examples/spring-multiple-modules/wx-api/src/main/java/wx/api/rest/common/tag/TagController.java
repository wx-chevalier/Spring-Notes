package wx.api.rest.common.tag;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.common.data.infra.TagType;
import wx.common.data.shared.id.*;
import wx.domain.infra.tag.Tag;
import wx.domain.infra.tag.TagRepository;
import wx.infra.common.exception.NotFoundException;

@RestController
@RequestMapping("/tag")
@Api(tags = "🏷 标签相关接口")
public class TagController extends BaseController {

  private TagRepository tagRepository;

  public TagController(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @GetMapping
  @ApiOperation(value = "✅ 获取当前可用的指定类型的标签")
  public ResponseEnvelope<Collection<Tag>> list(
      @RequestParam(value = "type", required = false) TagType tagType,
      @RequestParam(value = "key", required = false) String searchText) {
    Collection<Tag> tags;
    if (tagType != null) {
      tags = tagRepository.findByTagType(getTenantId(), tagType);
    } else {
      tags = tagRepository.find(getTenantId());
    }
    if (StringUtils.hasText(searchText)) {
      tags =
          tags.stream()
              .filter(tag -> tag.getTag().contains(searchText))
              .collect(Collectors.toList());
    }
    return ResponseEnvelope.createOk(tags);
  }

  @PostMapping
  @ApiOperation(value = "✅ 创建新的标签")
  public ResponseEnvelope<Tag> add(@RequestBody @Valid TagAddRequest request) {

    TenantId tenantId = getTenantId();
    Optional<Tag> tag = tagRepository.findTag(tenantId, request.getType(), request.getTag());
    if (tag.isPresent()) {
      throw new NotFoundException("创建失败, 该标签已存在");
    }

    return ResponseEnvelope.createOk(
        tagRepository.save(
            tenantId, new Tag(tenantId, request.getTag(), getCurrentUserId(), request.getType())));
  }

  @DeleteMapping("/{tagType}/{tag}")
  @ApiOperation(value = "✅ 禁用某标签")
  public ResponseEnvelope<Void> disabled(
      @PathVariable("tagType") TagType tagType, @PathVariable("tag") String tag) {
    tagRepository.removeTag(getTenantId(), tagType, tag);
    return ResponseEnvelope.createOk();
  }
}
