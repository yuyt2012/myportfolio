package portfolio.webaplication.dto;

        import lombok.*;
        import portfolio.webaplication.entity.MemberEntity;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDTO {

    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberName;

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        return MemberDTO.builder()
                .id(memberEntity.getId())
                .memberEmail(memberEntity.getMemberEmail())
                .memberPassword(memberEntity.getMemberPassword())
                .memberName(memberEntity.getMemberName())
                .build();
    }
}
