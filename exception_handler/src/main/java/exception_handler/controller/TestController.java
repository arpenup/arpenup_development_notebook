package exception_handler.controller;

import com.google.common.collect.ImmutableMap;
import exception_handler.entity.WebResp;
import exception_handler.exception.InternalServerErrorException;
import exception_handler.exception.RequestValidationFailedException;
import exception_handler.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @ResponseBody
    @RequestMapping(value = "/test", method = {RequestMethod.GET})
    public WebResp<String> test(@RequestParam(value = "signal") int signal) {

        if (signal == 0) {
            return WebResp.success("测试成功", "测试数据");
        } else if (signal == 1) {
            throw new ResourceNotFoundException(ImmutableMap.of("请求异常", "请求资源不存在"));
        } else if (signal == 2) {
            int[] ints = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
            try {
                int i = ints[10];
                System.out.println("i = " + i);
            } catch (Exception e) {
                throw new InternalServerErrorException(ImmutableMap.of("请求异常", "<UNK>"), e.getCause());
            }
            return WebResp.success("测试成功", "获取数据成功");
        } else {
            throw new RequestValidationFailedException(ImmutableMap.of("请求异常", "参数不合法，signal只能为0、1、2"));
        }

    }

}
