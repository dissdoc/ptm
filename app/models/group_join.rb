class GroupJoin < ActiveRecord::Base
  belongs_to :user
  belongs_to :group

  validates :user_id, :group_id, :role, :presence => true
  validates :user_id, :uniqueness => { :scope => :group_id, :message => "should happen once per club" }
  validates :role, :inclusion => { :in => %w(admin member), :message => "Role %{value} is reserved" }

  def accepted!
    update_attribute(:accepted, true)
  end

  def reject!
    update_attribute(:accepted, false)
  end
end
