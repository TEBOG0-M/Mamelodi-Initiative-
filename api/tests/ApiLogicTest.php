<?php
declare(strict_types=1);
use PHPUnit\Framework\TestCase;
require_once __DIR__ . '/../src/lib.php';

final class ApiLogicTest extends TestCase {
    private function good(): array {
        return ['full_name'=>'Thabo Nkosi','email'=>'thabo@example.co.za','phone'=>'+27 82 123 4567',
          'qualification'=>'BSc 2nd year','field_of_study'=>'Mathematics','programme_preference'=>'summer_jam',
          'subjects'=>['Mathematics'],'availability'=>['Saturday Mornings'],
          'motivation'=>'I want to help learners improve their maths marks.','consent_background_check'=>true];
    }
    public function testValidApplicationPasses(): void { [, $e] = validate_application($this->good()); $this->assertSame([], $e); }
    public function testMissingConsentFails(): void { $d=$this->good(); $d['consent_background_check']=false; [, $e]=validate_application($d); $this->assertArrayHasKey('consent_background_check',$e); }
    public function testBadProgrammeEmailSubjectFail(): void {
        $d=$this->good(); $d['programme_preference']='x'; $d['email']='nope'; $d['subjects']=['Hacking'];
        [, $e]=validate_application($d);
        $this->assertArrayHasKey('programme_preference',$e); $this->assertArrayHasKey('email',$e); $this->assertArrayHasKey('subjects',$e);
    }
    public function testNonStringInputsDoNotCrash(): void { [, $e]=validate_application(['full_name'=>['x'],'subjects'=>'a']); $this->assertNotEmpty($e); }
    public function testClaimsAccepted(): void { $u=check_claims(['aud'=>'p','iss'=>'https://securetoken.google.com/p','sub'=>'u1','email'=>'a@b.co'],'p'); $this->assertSame('u1',$u['uid']); }
    public function testWrongAudienceRejected(): void { $this->expectException(RuntimeException::class); check_claims(['aud'=>'other','iss'=>'https://securetoken.google.com/p','sub'=>'u1'],'p'); }
    public function testWrongIssuerRejected(): void { $this->expectException(RuntimeException::class); check_claims(['aud'=>'p','iss'=>'https://evil.example','sub'=>'u1'],'p'); }
    public function testEmptySubRejected(): void { $this->expectException(RuntimeException::class); check_claims(['aud'=>'p','iss'=>'https://securetoken.google.com/p','sub'=>''],'p'); }
    public function testContactValidation(): void { [, $e]=validate_contact(['name'=>'A','email'=>'x','subject'=>'hi','message'=>'short']); $this->assertCount(4,$e); }
}